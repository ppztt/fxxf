package net.mingsoft.fxxf.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.fxxf.entity.Record;
import net.mingsoft.fxxf.entity.*;
import net.mingsoft.fxxf.request.BasePageResult;
import net.mingsoft.fxxf.request.FeedBackCompanyPageRequest;
import net.mingsoft.fxxf.service.*;
import net.mingsoft.fxxf.service.impl.MyFeedbackService;
import net.mingsoft.fxxf.vo.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private FeedbackStatService feedbackStatService;

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

}
