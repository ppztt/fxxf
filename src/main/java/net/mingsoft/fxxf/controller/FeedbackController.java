package net.mingsoft.fxxf.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.anno.OperatorLogAnno;
import net.mingsoft.fxxf.entity.Applicants;
import net.mingsoft.fxxf.entity.Feedback;
import net.mingsoft.fxxf.entity.Record;
import net.mingsoft.fxxf.entity.User;
import net.mingsoft.fxxf.service.ApplicantsService;
import net.mingsoft.fxxf.service.FeedbackService;
import net.mingsoft.fxxf.service.RecordService;
import net.mingsoft.fxxf.service.UserService;
import net.mingsoft.fxxf.service.impl.MyFeedbackService;
import net.mingsoft.fxxf.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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

    /**
     *  查询企业投诉数量和已处理数量统计数据
     */
    // @RequiresPermissions("wlythcn:jdts")
    @GetMapping("/countByApplicantList")
    @ApiOperation(value = "监督投诉-列表", notes = "监督投诉-列表")
    @OperatorLogAnno(operType = "查询", operModul = "放心消费承诺单位/无理由退货承诺单位", operDesc = "监督投诉-列表")
    public BaseResult<BasePageResult<FeedbackComplaintVo>> countByApplicantList(ApplicantBasePageRequest applicantBasePageRequest) {
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



}
