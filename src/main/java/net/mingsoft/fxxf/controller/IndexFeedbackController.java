package net.mingsoft.fxxf.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.fxxf.bean.base.BaseResult;
import net.mingsoft.fxxf.bean.entity.*;
import net.mingsoft.fxxf.bean.vo.FeedbackMsgVo;
import net.mingsoft.fxxf.mapper.FeedbackMapper;
import net.mingsoft.fxxf.mapper.FeedbackTypeMapper;
import net.mingsoft.fxxf.service.FeedbackService;
import net.mingsoft.fxxf.service.UserService;
import org.apache.commons.compress.utils.Lists;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 留言反馈
 */
@Api(tags = {"用户端留言反馈相关接口"})
@RestController
@RequestMapping("/feedback")
@Slf4j
public class IndexFeedbackController {

    @Resource
    private FeedbackService feedbackService;

    @Resource
    private UserService userService;

    @Resource
    FeedbackTypeMapper feedbackTypeMapper;

    @Value("${saveAttachmentPath}")
    private String feedbackAbsolutePath;

    @Resource
    private FeedbackMapper feedbackMapper;




    @ApiOperation(value = "保存留言反馈", notes = "留言反馈/保存留言反馈")
    @PostMapping("/saveFeedbackInfo")
    @DynamicParameters(
            name = "feedback",
            properties = {
                    @DynamicParameter(name = "feedback", value = "留言反馈实体类", required = true)
            })
    public BaseResult<String> saveFeedbackInfo(@RequestBody Feedback feedback) {
        try {
            Integer applicantsId = feedback.getApplicantsId();
            if (applicantsId != null) {
                LocalDateTime now = LocalDateTime.now();
                feedback.setCreateTime(now);
                feedback.setUpdateTime(now);
                //状态默认未处理
                feedback.setStatus("0");
                feedback.setIsNew(1);

                //解析附件信息
                List<FeedbackUpload> fileList = feedback.getFileList();
                String filesInfo = JSON.toJSONString(fileList);
                feedback.setFilesInfo(filesInfo);

                feedbackService.save(feedback);
                return BaseResult.success();
            } else {
                return new BaseResult<>("500", "请选择经营注册企业");
            }
        } catch (Exception e) {
            log.error("保存留言反馈发生异常", e);
            return BaseResult.fail();
        }
    }

    @ApiOperation(value = "经营注册者列表", notes = "留言反馈/经营注册者列表")
    @GetMapping("/companyList")
    public BaseResult<ArrayList<Applicants>> companyList(@RequestParam(required = false) @ApiParam(name = "keyword", value = "关键字") String keyword) {
        try {
            ArrayList<Applicants> list = feedbackService.companyList(keyword);
            return BaseResult.success(list);
        } catch (Exception e) {
            log.error("保存留言反馈发生异常", e);
            return BaseResult.fail();
        }
    }

    @ApiOperation(value = "反馈类型", notes = "留言反馈/反馈类型")
    @GetMapping("/feedbackType")
    public BaseResult<ArrayList<FeedbackType>> feedbackType(@RequestParam(required = false, defaultValue = "0") @ApiParam(name = "flag", value = "旗标值：0前台；1后台") Integer flag) {
        try {
            ArrayList<FeedbackType> feedbackTypeList = feedbackTypeMapper.feedbackType(flag);
            return BaseResult.success(feedbackTypeList);
        } catch (Exception e) {
            log.error("反馈类型/反馈原因 查询发生异常", e);
            return BaseResult.fail();
        }
    }

    @ApiOperation(value = "反馈原因", notes = "留言反馈/反馈原因")
    @GetMapping("/feedbackReason")
    public BaseResult<ArrayList<FeedbackType>> feedbackReason(@RequestParam @ApiParam(name = "id", value = "类型id", required = true) Integer id) {
        try {
            ArrayList<FeedbackType> feedbackTypeList = feedbackTypeMapper.feedbackReason(id);
            return BaseResult.success(feedbackTypeList);
        } catch (Exception e) {
            log.error("反馈类型/反馈原因 查询发生异常", e);
            return BaseResult.fail();
        }
    }

    @ApiOperation(value = "留言反馈温馨提示", notes = "留言反馈温馨提示")
    @GetMapping(value = "/msg")
    public BaseResult<FeedbackMsgVo> feedbackMsg() {
        try {
            // 获取登录用户
            ManagerEntity managerEntity = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
            // roleId == 1 ，说明是管理员，可以查看全部，否则根据地市去查
            Integer roleId = managerEntity.getRoleId();


            User user = userService.getById(managerEntity.getIntegerId());
            FeedbackMsgVo feedbacks = feedbackMapper.getMsgCnt(roleId, user.getCity(), user.getDistrict());

            return BaseResult.success(feedbacks);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return BaseResult.fail();
    }


    @ApiOperation(value = "附件批量上传", notes = "留言反馈/附件批量上传")
    @PostMapping(value = "/uploadFile", produces = "application/json;charset=UTF-8")
    public BaseResult<ArrayList<FeedbackUpload>> attachmentUpload(@RequestParam("files") @ApiParam(name = "files", value = "附件：任意数据格式；文件最大限制500M") MultipartFile[] files) {

        // 限制为zip和图片格式（zip、jpg、jpeg、png），检查下上传文件大小校验10M以下
        boolean match = Arrays.stream(files).noneMatch(f -> Objects.requireNonNull(f.getOriginalFilename()).endsWith(".zip")
                || f.getOriginalFilename().endsWith(".jpg")
                || f.getOriginalFilename().endsWith(".jpeg")
                || f.getOriginalFilename().endsWith(".png"));
        if(match){
            return BaseResult.fail("上传失败，请上传zip、jpg、jpeg、png格式文件");
        }
        match = Arrays.stream(files).anyMatch(f -> f.getSize() > 10485760);

        if(match){
            return BaseResult.fail("上传失败，请上传10M内的文件", null);
        }
        ArrayList<FeedbackUpload> resultList = Lists.newArrayList();
        FeedbackUpload feedbackUpload;
        //1.获取文件名、文件流
        InputStream in;
        String uuid;
        String fileName;
        //绝对路径
        String absolutePath;
        //相对路径
        String relativePath;

        for (MultipartFile file : files) {
            feedbackUpload = new FeedbackUpload();
            uuid = UUID.randomUUID().toString();
            fileName = file.getOriginalFilename();
            //UUID保证文件名称的唯一
            absolutePath = feedbackAbsolutePath + File.separator + uuid + fileName;
            relativePath = feedbackAbsolutePath + File.separator + uuid + fileName;
            try (FileOutputStream fos = new FileOutputStream(absolutePath)){
                //2.写入磁盘
                in = file.getInputStream();
                byte[] b = new byte[1024];
                int len;
                while ((len = in.read(b)) != -1) {
                    fos.write(b, 0, len);
                }
                in.close();
                log.info("附件成功写入磁盘：" + absolutePath);
                //组装实体类
                feedbackUpload.setFileName(fileName);
                feedbackUpload.setPath(relativePath);
                resultList.add(feedbackUpload);
            } catch (IOException e) {
                log.error("留言反馈--保存附件发生异常:", e);
                return BaseResult.fail();
            }
        }
        //3.返回文件名、文件存储路径
        return BaseResult.success(resultList);
    }

}
