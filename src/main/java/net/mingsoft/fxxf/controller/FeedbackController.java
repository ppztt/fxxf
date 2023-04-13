package net.mingsoft.fxxf.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.anno.OperatorLogAnno;
import net.mingsoft.fxxf.service.FeedbackService;
import net.mingsoft.fxxf.vo.ApplicantBasePageRequest;
import net.mingsoft.fxxf.vo.BasePageResult;
import net.mingsoft.fxxf.vo.BaseResult;
import net.mingsoft.fxxf.vo.FeedbackComplaintVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 留言反馈
 */
@Api(tags = {"留言反馈相关接口"})
@RestController
@RequestMapping("/feedback")
@Slf4j
public class FeedbackController {

    @Resource
    private FeedbackService feedbackService;
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

}
