package net.mingsoft.fxxf.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.fxxf.entity.Applicants;
import net.mingsoft.fxxf.entity.Feedback;
import net.mingsoft.fxxf.entity.User;
import net.mingsoft.fxxf.mapper.ApplicantsMapper;
import net.mingsoft.fxxf.mapper.FeedbackMapper;
import net.mingsoft.fxxf.mapper.UserMapper;
import net.mingsoft.fxxf.service.FeedbackService;
import net.mingsoft.fxxf.vo.ApplicantBasePageRequest;
import net.mingsoft.fxxf.vo.BasePageResult;
import net.mingsoft.fxxf.vo.FeedbackComplaintVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    UserMapper userMapper;

    @Resource
    FeedbackMapper feedbackMapper;

    @Override
    public List<Applicants> companyList(String keyword) {

        List<Applicants> applicantsList = applicantsMapper.companyList(keyword);
        return applicantsList;
    }

    /**
     * 询企业投诉数量和已处理数量统计数据
     * @return BasePageResult<FeedbackComplaintVo>
     */
    @Override
    public BasePageResult<FeedbackComplaintVo> countByApplicantList(ApplicantBasePageRequest applicantBasePageRequest) {
        // 获取登录用户
        Subject currentSubject = SecurityUtils.getSubject();
        ManagerEntity manager = (ManagerEntity) currentSubject.getPrincipal();
        Integer roleId = manager.getRoleId();

        User user = userMapper.selectById(manager.getId());
        Map<String, Object> map = new HashMap<>();

        map.put("roleId", roleId);
        map.put("city", user.getCity());
        map.put("district", user.getDistrict());
        map.put("type", applicantBasePageRequest.getType());
        map.put("search", applicantBasePageRequest.getSearch());
        IPage<FeedbackComplaintVo> feedbackIPage = feedbackMapper.feedbackList(new Page<>(applicantBasePageRequest.getCurrent(), applicantBasePageRequest.getSize()), map);
        return new BasePageResult<>(feedbackIPage.getCurrent(), feedbackIPage.getSize(), feedbackIPage.getPages(), feedbackIPage.getTotal(), feedbackIPage.getRecords());
    }

}
