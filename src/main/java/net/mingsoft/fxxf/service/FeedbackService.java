package net.mingsoft.fxxf.service;


import com.baomidou.mybatisplus.extension.service.IService;
import net.mingsoft.fxxf.entity.Feedback;
import net.mingsoft.fxxf.entity.FeedbackStat;
import net.mingsoft.fxxf.vo.ApplicantBasePageRequest;
import net.mingsoft.fxxf.vo.BasePageResult;
import net.mingsoft.fxxf.vo.FeedbackComplaintVo;
import net.mingsoft.fxxf.vo.FeedbackStatisticRequest;

import java.util.List;

/**
 * <p>
 * 留言反馈 服务类
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-09
 */
public interface FeedbackService extends IService<Feedback> {

    /**
     * 询企业投诉数量和已处理数量统计数据
     * @return BasePageResult<FeedbackComplaintVo>
     */
    BasePageResult<FeedbackComplaintVo> countByApplicantList(ApplicantBasePageRequest applicantBasePageRequest);

    /**
     * 监督投诉统计
     */
    List<FeedbackStat> statistic(FeedbackStatisticRequest feedbackStatisticRequest);


}
