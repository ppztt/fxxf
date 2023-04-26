package net.mingsoft.fxxf.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.fxxf.bean.entity.Attachment;
import net.mingsoft.fxxf.bean.base.BasePageResult;
import net.mingsoft.fxxf.bean.vo.ApplicantsExtend;
import net.mingsoft.fxxf.bean.base.BaseResult;
import net.mingsoft.fxxf.service.AttachmentService;
import net.mingsoft.fxxf.service.IndexService;
import net.mingsoft.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * @ClassName: IndexController
 * @Description 首页管理
 * @Author Ligy
 * @Date 2020/1/13 18:06
 **/
@Api(tags = "首页管理")
@RestController
@RequestMapping("/${ms.manager.path}/index")
public class IndexController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    IndexService indexService;

    @Resource
    AttachmentService attachmentService;

    @ApiOperation(value = "首页列表搜索", notes = "首页管理/首页列表搜索")
    @GetMapping("/searchList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", required = true),
            @ApiImplicitParam(name = "size", value = "每页展示条数", required = true),
            @ApiImplicitParam(name = "keyword", value = "关键字：企业名称、时间", required = false),
            @ApiImplicitParam(name = "type", value = "申报单位类型(1:放心消费单位； 2:无理由退货实体店)", required = true),
            @ApiImplicitParam(name = "status", value = "状态(1:在期； 0:摘牌)", required = true)
    })
    public BaseResult<Page<ApplicantsExtend>> searchList(@RequestParam(name = "current", defaultValue = "1") Integer current,
                                                         @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                         @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                                         @RequestParam(name = "type", defaultValue = "1") String type,
                                                         @RequestParam(name = "status", defaultValue = "1") String status) {
        Page<ApplicantsExtend> page = indexService.searchList(current, size, keyword, type, status);
        return BaseResult.success(page);
    }

    @ApiOperation(value = "放心消费承诺单位-在期经营者公示", notes = "首页管理/放心消费承诺单位-在期经营者公示")
    @GetMapping("/fxPublicOnline")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", required = true),
            @ApiImplicitParam(name = "size", value = "每页展示条数", required = true),
            @ApiImplicitParam(name = "keyword", value = "关键字：企业名称、时间", required = false)
    })
    public BaseResult<Page<ApplicantsExtend>> fxPublicOnline(@RequestParam(name = "current", defaultValue = "1") Integer current,
                                                             @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                             @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        String type = "1";
        String status = "1";
        Page<ApplicantsExtend> page = indexService.searchList(current, size, null, type, status);
        return BaseResult.success(page);
    }

    @ApiOperation(value = "放心消费承诺单位-摘牌经营者公示", notes = "首页管理/放心消费承诺单位-摘牌经营者公示")
    @GetMapping("/fxPublicOffline")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", required = true),
            @ApiImplicitParam(name = "size", value = "每页展示条数", required = true),
            @ApiImplicitParam(name = "keyword", value = "关键字：企业名称、时间", required = false)
    })
    public BaseResult<Page<ApplicantsExtend>> fxPublicOffline(@RequestParam(name = "current", defaultValue = "1") Integer current,
                                                              @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                              @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        String type = "1";
        String status = "0";
        Page<ApplicantsExtend> page = indexService.searchList(current, size, null, type, status);
        return BaseResult.success(page);
    }

    @ApiOperation(value = "无理由退货承诺实体店-在期经营者公示", notes = "首页管理/无理由退货承诺实体店-在期经营者公示")
    @GetMapping("/noReasonReturnPublicOnline")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", required = true),
            @ApiImplicitParam(name = "size", value = "每页展示条数", required = true),
            @ApiImplicitParam(name = "keyword", value = "关键字：企业名称、时间", required = false)
    })
    public BaseResult<Page<ApplicantsExtend>> noReasonReturnPublicOnline(@RequestParam(name = "current", defaultValue = "1") Integer current,
                                                                         @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                                         @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        String type = "2";
        String status = "1";
        Page<ApplicantsExtend> page = indexService.searchList(current, size, null, type, status);
        return BaseResult.success(page);
    }

    @ApiOperation(value = "无理由退货承诺实体店-摘牌经营者公示", notes = "首页管理/无理由退货承诺实体店-摘牌经营者公示")
    @GetMapping("/noReasonReturnPublicOffline")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", required = true),
            @ApiImplicitParam(name = "size", value = "每页展示条数", required = true),
            @ApiImplicitParam(name = "keyword", value = "关键字：企业名称、时间", required = false)
    })
    public BaseResult<Page<ApplicantsExtend>> noReasonReturnPublicOffline(@RequestParam(name = "current", defaultValue = "1") Integer current,
                                                                          @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                                          @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        String type = "2";
        String status = "0";
        Page<ApplicantsExtend> page = indexService.searchList(current, size, null, type, status);
        return BaseResult.success(page);
    }

    @ApiOperation(value = "资料下载", notes = "首页管理/资料下载")
    @GetMapping("/downloadAttachment")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "文件id", required = true)
    })
    public BaseResult downloadAttachment(Integer id, HttpServletRequest request, HttpServletResponse response) {
        if (id == null) {
            return BaseResult.fail("文件id不允许为空");
        }
        Attachment attachment = attachmentService.getById(id);
        String filePath = attachment.getStorage();
        String fileName = attachment.getFilename();
        //根据文件路径、文件id下载文件
        File file = new File(filePath);
        try {
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = response.getOutputStream();

            // 配置文件下载
            response.setHeader("content-type", "application/octet-stream;application/json");
            response.setContentType("application/octet-stream");
            // 下载文件能正常显示中文
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = fis.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }

            fis.close();
            os.close();
        } catch (IOException e) {
            log.error("文件下载失败：{}", e);
            return BaseResult.fail();
        }
        return BaseResult.success();
    }

    @ApiOperation(value = "资料列表", notes = "首页管理/资料列表")
    @GetMapping("/attachmentList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", required = true),
            @ApiImplicitParam(name = "size", value = "每页展示条数", required = true),
            @ApiImplicitParam(name = "keyword", value = "关键字：资料名称、创建时间", required = false)

    })
    public BaseResult<BasePageResult<Attachment>> attachmentList(@RequestParam Integer current, @RequestParam Integer size, String keyword) {
        try {
            IPage<Attachment> attachmentListPage = attachmentService.page(
                    new Page<>(current, size),
                    new QueryWrapper<Attachment>()
                            .like(StringUtils.isNotBlank(keyword), "filename", keyword)
                            .or()
                            .like(StringUtils.isNotBlank(keyword), "uploader", keyword)
                            .orderByDesc("create_time"));
            return BaseResult.success(new BasePageResult<>(attachmentListPage.getCurrent(), attachmentListPage.getSize(),
                    attachmentListPage.getPages(), attachmentListPage.getTotal(), attachmentListPage.getRecords()));
        } catch (Exception e) {
            log.error("资料列表查询发生异常:{}", e);
            return BaseResult.fail();
        }
    }

    @ApiOperation(value = "查看 放心消费/无理由退货 公告详情", notes = "首页管理/查看 \"放心消费/无理由退货\" 公告详情")
    @GetMapping("/detail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "公告id")
    })
    public BaseResult<ApplicantsExtend> detail(@RequestParam Integer id) {
        ApplicantsExtend applicants = indexService.getById(id);
        //格式化日期：由 yyyy-MM-dd 修改为 yyyy-MM
        String startTime = applicants.getStartTime();
        String endTime = applicants.getEndTime();
        //无理由退换货类型无有效期时间，不需要转换日期
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            startTime = DateUtil.format(DateUtil.parse(startTime, DateUtil.FORMAT_SHORT_DATE), DateUtil.FORMAT_SHORT_DATE);
            endTime = DateUtil.format(DateUtil.parse(endTime, DateUtil.FORMAT_SHORT_DATE), DateUtil.FORMAT_SHORT_DATE);
            applicants.setStartTime(startTime);
            applicants.setEndTime(endTime);
        }
        try {
            return BaseResult.success(applicants);
        } catch (Exception e) {
            log.error("查看 放心消费/无理由退货 公告详情发生异常：{}", e);
            return BaseResult.fail();
        }
    }
}

