package net.mingsoft.fxxf.service.impl;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.bean.base.BasePageResult;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.bean.entity.Feedback;
import net.mingsoft.fxxf.bean.entity.FeedbackStat;
import net.mingsoft.fxxf.bean.request.FeedBackCompanyPageRequest;
import net.mingsoft.fxxf.bean.request.FeedbackStatisticRequest;
import net.mingsoft.fxxf.bean.vo.FeedbackComplaintVo;
import net.mingsoft.fxxf.bean.vo.ManagerInfoVo;
import net.mingsoft.fxxf.mapper.ApplicantsMapper;
import net.mingsoft.fxxf.mapper.FeedbackMapper;
import net.mingsoft.fxxf.mapper.FeedbackStatMapper;
import net.mingsoft.fxxf.service.FeedbackService;
import net.mingsoft.fxxf.service.ManagerInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 留言反馈 服务实现类
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-09
 */
@Slf4j
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    @Resource
    ApplicantsMapper applicantsMapper;

    @Resource
    private ManagerInfoService managerInfoService;

    @Resource
    FeedbackMapper feedbackMapper;

    @Resource
    FeedbackStatMapper feedbackStatMapper;

    /**
     * 询企业投诉数量和已处理数量统计数据
     */
    @Override
    public BasePageResult<FeedbackComplaintVo> countByApplicantList(FeedBackCompanyPageRequest feedBackCompanyPageRequest) {
        ManagerInfoVo loginUserInfo = managerInfoService.getLoginUserInfo();
        Map<String, Object> map = new HashMap<>();

        map.put("roleId", loginUserInfo.getRoleIds());
        map.put("city", loginUserInfo.getCity());
        map.put("district", loginUserInfo.getDistrict());
        map.put("type", feedBackCompanyPageRequest.getType());
        map.put("search", feedBackCompanyPageRequest.getSearch());
        IPage<FeedbackComplaintVo> feedbackIPage = feedbackMapper.feedbackList(new Page<>(
                feedBackCompanyPageRequest.getCurrent(), feedBackCompanyPageRequest.getSize()), map);

        return new BasePageResult<>(feedbackIPage.getCurrent(), feedbackIPage.getSize(), feedbackIPage.getPages(),
                feedbackIPage.getTotal(), feedbackIPage.getRecords());
    }

    /**
     * 监督投诉统计
     */
    @Override
    public List<FeedbackStat> statistic(FeedbackStatisticRequest feedbackStatisticRequest) {
        List<FeedbackStat> statList;
        //根据角色id选择统计维度
        ManagerInfoVo loginUserInfo = managerInfoService.getLoginUserInfo();
        int roleId = loginUserInfo.getRoleIds();

        // TODO 后续优化
        FeedbackStat feedback = new FeedbackStat();
        feedback.setStartTime(DateUtil.format(feedbackStatisticRequest.getStartTime(), "yyyy-MM-dd"));
        feedback.setEndTime(DateUtil.format(feedbackStatisticRequest.getEndTime(), "yyyy-MM-dd"));
        feedback.setType(feedbackStatisticRequest.getType().toString());
        feedback.setRoleId(roleId);
        if (roleId == 1) {
            //系统管理员
            statList = feedbackStatMapper.statListByAdminRole(feedback, null);
        } else {
            //地市管理员
            if (StringUtils.isEmpty(loginUserInfo.getCity())) {
                log.info("当前登录用户归属地市为Null，不执行查询;返回空集合");
                statList = Lists.newArrayList();
            } else {
                feedback.setCity(loginUserInfo.getCity());
                statList = feedbackStatMapper.statListByCityRole(feedback, null);
            }
        }
        return statList;
    }

    @Override
    public ArrayList<Applicants> companyList(String keyword) {
        ArrayList<Applicants> applicantsList = applicantsMapper.companyList(keyword);
        return applicantsList;
    }

}
