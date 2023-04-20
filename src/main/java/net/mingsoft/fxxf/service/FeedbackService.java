package net.mingsoft.fxxf.service;


import com.baomidou.mybatisplus.extension.service.IService;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.bean.entity.Feedback;
import net.mingsoft.fxxf.bean.entity.FeedbackStat;
import net.mingsoft.fxxf.bean.base.BasePageResult;
import net.mingsoft.fxxf.bean.request.FeedBackCompanyPageRequest;
import net.mingsoft.fxxf.bean.vo.FeedbackComplaintVo;
import net.mingsoft.fxxf.bean.request.FeedbackStatisticRequest;

import java.util.ArrayList;
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
     *
     * @return BasePageResult<FeedbackComplaintVo>
     */
    BasePageResult<FeedbackComplaintVo> countByApplicantList(FeedBackCompanyPageRequest feedBackCompanyPageRequest);

    /**
     * 监督投诉统计
     */
    List<FeedbackStat> statistic(FeedbackStatisticRequest feedbackStatisticRequest);


    ArrayList<Applicants> companyList(String keyword);


}
