package net.mingsoft.fxxf.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.fxxf.bean.base.BasePageResult;
import net.mingsoft.fxxf.bean.base.BaseResult;
import net.mingsoft.fxxf.bean.entity.Record;
import net.mingsoft.fxxf.bean.entity.*;
import net.mingsoft.fxxf.bean.request.*;
import net.mingsoft.fxxf.bean.vo.*;
import net.mingsoft.fxxf.mapper.FeedbackMapper;
import net.mingsoft.fxxf.mapper.FeedbackTypeMapper;
import net.mingsoft.fxxf.service.ApplicantsService;
import net.mingsoft.fxxf.service.FeedbackService;
import net.mingsoft.fxxf.service.RecordService;
import net.mingsoft.fxxf.service.UserService;
import net.mingsoft.fxxf.service.impl.MyFeedbackService;
import org.apache.commons.compress.utils.Lists;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 留言反馈
 */
@Api(tags = {"留言反馈相关接口"})
@RestController
@RequestMapping("/${ms.manager.path}/feedback")
@Slf4j
public class FeedbackController {

    @Resource
    private FeedbackService feedbackService;

    @Resource
    private ApplicantsService applicantsService;

    @Resource
    private RecordService recordService;

    @Resource
    private UserService userService;

    @Resource
    private MyFeedbackService myFeedbackService;

    @Resource
    FeedbackTypeMapper feedbackTypeMapper;

    @Value("${saveAttachmentPath}")
    private String feedbackAbsolutePath;

    @Resource
    private FeedbackMapper feedbackMapper;

    /**
     *  查询企业投诉数量和已处理数量统计数据
     */
    // @RequiresPermissions("wlythcn:jdts")
    @GetMapping("/countByApplicantList")
    @ApiOperation(value = "监督投诉-列表", notes = "监督投诉-列表")
    public BaseResult<BasePageResult<FeedbackComplaintVo>> countByApplicantList(FeedBackCompanyPageRequest applicantBasePageRequest) {
        try {

            return BaseResult.success(feedbackService.countByApplicantList(applicantBasePageRequest));
        } catch (Exception e) {
            log.error("获取放心消费承诺单位表格数据失败", e);
            return BaseResult.fail();
        }
    }


    /**
     * 企业投诉反馈列表
     */
    // @RequiresPermissions("fxxfcn:jdts")
    @GetMapping("/companyDetails")
    @ApiOperation(value = "企业监督投诉明细列表", notes = "企业监督投诉明细列表")
    public BaseResult<BasePageResult<FeedbackCompanyDetailsVo>> companyDetails(FeedbackPageRequest feedbackPageRequest) {
        try {
            IPage<Feedback> feedbacks = feedbackService.page(
                    new Page<>(feedbackPageRequest.getCurrent(), feedbackPageRequest.getSize()),
                    new QueryWrapper<Feedback>().lambda()
                            .eq(Feedback::getApplicantsId, feedbackPageRequest.getApplicantsId())
                            .ge(Objects.nonNull(feedbackPageRequest.getStartTime()), Feedback::getCreateTime, feedbackPageRequest.getStartTime())
                            .le(Objects.nonNull(feedbackPageRequest.getEndTime()), Feedback::getCreateTime, feedbackPageRequest.getEndTime())
                            .orderByDesc(Feedback::getIsNew)
                            .orderByDesc(Feedback::getCreateTime));

            List<FeedbackCompanyDetailsVo> feedbackCompanyDetails = new ArrayList<>();
            feedbacks.getRecords().forEach(feedback -> {
                FeedbackCompanyDetailsVo feedbackCompanyDetailsVo = new FeedbackCompanyDetailsVo();
                BeanUtils.copyProperties(feedback, feedbackCompanyDetailsVo);
                feedbackCompanyDetails.add(feedbackCompanyDetailsVo);
            });
            return BaseResult.success(new BasePageResult<>(feedbacks.getCurrent(), feedbacks.getSize(), feedbacks.getPages(), feedbacks.getTotal(), feedbackCompanyDetails));
        } catch (Exception e) {
            log.error("查询企业投诉反馈列表异常", e);
            return BaseResult.fail(e.getMessage());
        }
    }

    /**
     * 根据 id 查询监督投诉
     */
    // @RequiresPermissions("wlythcn:jdts")
    @GetMapping("/getFeedbackById/{id}")
    @ApiOperation(value = "监督投诉-企业详情处理", notes = "监督投诉-企业详情处理")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", value = "id", required = true),
            }
    )
    public BaseResult<FeedbackVo> getFeedbackById(@PathVariable(value = "id") String id) {
            // 根据 id 查询留言反馈
            Feedback feedback = feedbackService.getById(id);
            if (feedback == null) {
                return BaseResult.fail("留言不存在");
            }
            if (feedback.getIsNew() == 1) {
                feedback.setIsNew(0);
                feedbackService.updateById(feedback);
            }

            // 根据 applicants_id 查询归属地市
            Applicants applicants = applicantsService.getById(feedback.getApplicantsId());

            // 设置留言反馈归属地市
            if (applicants != null) {
                feedback.setCity(applicants.getCity());
            }

            // 根据 feedback_id 查询留言反馈操作记录
            List<Record> records = recordService.list(new QueryWrapper<Record>().eq("feedback_id", id).orderByDesc("create_time"));

            // 留言反馈操作记录设置操作用户
            records.forEach(recordItem -> {
                User user = userService.getById(recordItem.getUserid());
                recordItem.setUserName(user.getAccount());
            });
            feedback.setType("1".equals(feedback.getType()) ? "放心消费承诺单位" : "线下无理由退货承诺店");
            // 返回有操作记录的留言反馈对象
            FeedbackVo feedbackVo = new FeedbackVo(feedback, records, Objects.nonNull(applicants) ? applicants.getStatus() : null);
            return BaseResult.success(feedbackVo);
    }

    /**
     * 处理留言反馈并保存操作记录, 监督投诉-处理投诉
     */
    // @RequiresPermissions("wlythcn:jdts:process")
    @PostMapping("/handleFeedback/{id}")
    @ApiOperation(value = "监督投诉-处理投诉", notes = "监督投诉-处理投诉")
    public BaseResult<Integer> handleFeedback(@Validated FeedbackHandleRequest feedbackHandleRequest) {
        // 根据留言反馈id处理留言反馈并保存操作记录
        myFeedbackService.updateFeedback(feedbackHandleRequest.getId(), feedbackHandleRequest.getResult(), feedbackHandleRequest.getProcessingSituation());
        return BaseResult.success(feedbackHandleRequest.getId());
    }

    /**
     * 监督投诉统计
     */
    // @RequiresPermissions("wlythcn:jdtstj")
    @GetMapping("/statistic")
    @ApiOperation(value = "监督投诉统计-报表", notes = "监督投诉统计/监督投诉统计报表查询")
    public BaseResult<BasePageResult<FeedbackStat>> statistic(FeedbackStatisticRequest feedbackStatisticRequest) {
        BasePageResult<FeedbackStat> basePageResult = new BasePageResult<>();
        basePageResult.setRecords(feedbackService.statistic(feedbackStatisticRequest));
        return BaseResult.success(basePageResult);
    }

    // @RequiresPermissions("wlythcn:jdtstj")
    @GetMapping(value = "/exportStatistic")
    @ApiOperation(value = "监督投诉统计-导出Excel", notes = "监督投诉统计/监督投诉统计导出Excel")
    public void exportStatistic(FeedbackStatisticRequest feedbackStatisticRequest, HttpServletResponse response) {
        List<FeedbackStat> records = feedbackService.statistic(feedbackStatisticRequest);
        //根据角色动态更换Excel表头
        Subject currentSubject = SecurityUtils.getSubject();
        ManagerEntity manager = (ManagerEntity) currentSubject.getPrincipal();
        int roleId = manager.getRoleId();
        //Excel表头
        Map<String, String> titleMap = Maps.newLinkedHashMap();
        if (roleId == 1) {
            titleMap.put("city", "地市");
        } else {
            titleMap.put("city", "区县");
        }
        titleMap.put("companyTotal", "承诺单位数量");
        titleMap.put("complaintCompanyNum", "被反馈单位数量");
        titleMap.put("takeOff", "摘牌数量");
        titleMap.put("complaintTotal", "监督投诉的总条数");
        titleMap.put("unprocessed", "待处理");
        titleMap.put("warning", "督促告诫");
        titleMap.put("disqualification", "摘牌");
        titleMap.put("nonExistentComplaints", "投诉问题不存在");
        titleMap.put("other", "其他");

        File f = new File("监督投诉统计.xls");
        OutputStream out;
        try {
            out = Files.newOutputStream(f.toPath());
            net.mingsoft.utils.ExcelExportUtil.exportExcel(titleMap, records, out, "yyyy-MM-dd HH:mm:ss", response);
            out.close();
        } catch (IOException e) {
            log.error("导出留言反馈统计报表发生异常:", e);
        }
    }


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
    public BaseResult<ArrayList<FeedbackType>> feedbackReason(@RequestParam(required = true) @ApiParam(name = "id", value = "类型id", required = true) Integer id) {
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
        FileOutputStream fos;
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
            absolutePath = feedbackAbsolutePath + "/" + uuid + fileName;
            relativePath = feedbackAbsolutePath + "/" + uuid + fileName;
            try {
                //2.写入磁盘
                fos = new FileOutputStream(absolutePath);
                in = file.getInputStream();
                byte[] b = new byte[1024];
                int len;
                while ((len = in.read(b)) != -1) {
                    fos.write(b, 0, len);
                }
                in.close();
                fos.close();
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
