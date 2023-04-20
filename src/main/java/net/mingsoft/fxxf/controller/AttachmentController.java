package net.mingsoft.fxxf.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.fxxf.bean.entity.Attachment;
import net.mingsoft.fxxf.bean.vo.ApiResult;
import net.mingsoft.fxxf.service.AttachmentService;
import net.mingsoft.fxxf.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 资料管理 前端控制器
 *
 * @author Ligy
 * @since 2020-01-09
 */
@Api(tags = "资料管理")
@RestController
@RequestMapping("/attachment")
public class AttachmentController {

    private Logger log = LoggerFactory.getLogger(this.getClass());


    @Value("${saveAttachmentPath}")
    private String saveAttachmentPath;

    @Resource
    AttachmentService attachmentService;

    @Resource
    UserService userService;

    /**
     * @param keyword 关键字
     * @return ResponseBean
     * @author Ligy
     * @description 查询资料管理信息
     * @date 2020/1/9 15:41
     **/
    @ApiOperation(value = "查询资料列表", notes = "资料管理/资料列表")
    @GetMapping(value = "/info", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "current", value = "当前页", dataType = "int", example = "1", defaultValue = "1"),
                    @ApiImplicitParam(name = "size", value = "每页条数", dataType = "int", example = "10", defaultValue = "10"),
                    @ApiImplicitParam(name = "keyword", value = "关键字", dataType = "string", example = "张三", defaultValue = "张三")
            }
    )
    public ApiResult<IPage<Attachment>> attachmentInfo(@RequestParam(name = "current", defaultValue = "1") Integer current,
                                                       @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                       @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        IPage<Attachment> page = attachmentService.page(
                new Page<>(current, size),
                new QueryWrapper<Attachment>()
                        .like(StringUtils.isNotBlank(keyword), "filename", keyword)
                        .or()
                        .like(StringUtils.isNotBlank(keyword), "uploader", keyword)
                        .orderByDesc("create_time"));
        return ApiResult.success(page);
    }


    /**
     * @param files 一个或多个附件
     * @return ResponseBean
     * @author Ligy
     * @description 附件批量上传
     * @date 2020/1/9 15:50
     **/
    @ApiOperation(value = "附件批量上传", notes = "资料管理/附件批量上传")
    @PostMapping(value = "/uploadFile", produces = "application/json;charset=UTF-8", headers ="content-type=multipart/form-data")
    public ApiResult attachmentUpload(@ApiParam(name = "files", value = "附件：任意数据格式；文件最大限制500M") MultipartFile[] files) {
        InputStream in;
        String retMsg = "";
        FileOutputStream fos;
        for (MultipartFile file : files) {
            //附件名称唯一校验
            String fileName = file.getOriginalFilename();
            if (StringUtils.isNotBlank(fileName)) {
                List<Attachment> fList = attachmentService.list(new QueryWrapper<Attachment>().eq("filename", fileName));
                if (fList.size() > 0) {
                    //附件重名采取更名操作,添加时间后缀
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
                    String dateStr = LocalDateTime.now().format(formatter);
                    int index = fileName.indexOf(".");
                    fileName = fileName.substring(0, index) + "-" + dateStr + fileName.substring(index);
                    retMsg = "附件上传成功，附件重名采取更名操作，更名为【" + fileName + "】";
                } else {
                    //附件未重名
                    retMsg = "附件上传成功，文件名称：【" + fileName + "】";
                }
                try {
                    String storage = saveAttachmentPath + fileName;
                    fos = new FileOutputStream(storage);
                    in = file.getInputStream();
                    byte[] b = new byte[1024];
                    int len = 0;
                    while ((len = in.read(b)) != -1) {
                        fos.write(b, 0, len);
                    }
                    in.close();
                    fos.close();
                    log.info("附件成功写入磁盘：" + storage);

                    //记录上传时间、操作人、文件存储路径、文件名到 DB
                    ManagerEntity managerEntity = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
                    String uploader = managerEntity.getManagerName();
                    int downloads = 0;
                    LocalDateTime now = LocalDateTime.now();
                    Attachment attachment = new Attachment(fileName, storage, uploader, downloads, now, now);
                    attachmentService.save(attachment);
                } catch (IOException e) {
                    log.error("附件批量上传发生异常:", e);
                    return ApiResult.fail();
                }
            } else {
                log.info("文件名为空字符，无效附件不执行上传操作");
            }
        }
        return new ApiResult("200", retMsg);
    }

    /**
     * @param idArr 一个或多个附件id
     * @return java.lang.String
     * @author Ligy
     * @description 附件批量删除
     * @date 2020/1/9 15:52
     **/
    @ApiOperation(value = "附件批量删除", notes = "资料管理/附件批量删除")
    @PostMapping(value = "/del", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idArr", value = "附件id；1个或多个id用数组接收；数据请求样例：{\"idArr\": [1,2,3,4]}")
    })
    public ApiResult attachmentDel(@RequestBody JSONObject idArr) {
        JSONArray array = idArr.getJSONArray("idArr");
        List<Integer> list = array.toJavaObject(List.class);
        try {
            attachmentService.removeByIds(list);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("附件批量删除发生异常：{}", e);
            return ApiResult.fail();
        }
    }

    /**
     * @param id 附件id
     * @return ResponseBean
     * @author Ligy
     * @description
     * @date 2020/1/9 15:55
     **/
    @ApiOperation(value = "统计资料下载次数", notes = "资料管理/统计资料下载次数")
    @PostMapping(value = "/countById/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "资料id", required = true)
    })
    public ApiResult countDownloadById(@PathVariable(value = "id") int id) {
        try {
            Attachment entity = attachmentService.getById(id);
            entity.setDownloads(entity.getDownloads() + 1);
            entity.updateById();
            return ApiResult.success();
        } catch (Exception e) {
            log.error("资料下载次数统计异常：{}", e);
            return ApiResult.fail();
        }
    }
}

