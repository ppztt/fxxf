package net.mingsoft.fxxf.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.mapper.ApplicantsMapper;
import net.mingsoft.fxxf.mapper.FeedbackMapper;
import net.mingsoft.fxxf.entity.Applicants;
import net.mingsoft.fxxf.entity.Feedback;
import net.mingsoft.fxxf.service.FeedbackService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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

    @Override
    public List<Applicants> companyList(String keyword) {

        List<Applicants> applicantsList = applicantsMapper.companyList(keyword);
        return applicantsList;
    }

}
