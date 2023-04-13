package net.mingsoft.fxxf.service.impl;


import net.mingsoft.fxxf.entity.Feedback;
import net.mingsoft.fxxf.entity.Record;
import net.mingsoft.fxxf.entity.User;
import net.mingsoft.fxxf.service.FeedbackService;
import net.mingsoft.fxxf.service.RecordService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author laijunbao
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MyFeedbackService {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private RecordService recordService;

    /**
     * @param
     * @return
     * @throws
     * @description 根据留言反馈id处理留言反馈并保存操作记录
     * @author laijunbao
     * @updateTime 2020-01-11-0011 14:38
     */
    public void updateFeedback(String id, String result, String processingSituation) {
        // 更新留言反馈
        Feedback feedback = feedbackService.getById(id);
        if (!StringUtils.isNumeric(feedback.getType())) {
            feedback.setType("放心消费承诺单位".equals(feedback.getType()) ? "1" : "2");
        }
        feedback.setStatus("1");
        feedback.setResult(result);
        feedback.setUpdateTime(LocalDateTime.now());
        feedback.setProcTime(LocalDateTime.now());
        feedbackService.updateById(feedback);

        // 保存操作记录
        Record record = new Record();
        // 获取登录登录
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if (user != null) {
            record.setUserid(user.getId() + "");
        }
        record.setFeedbackId(Integer.parseInt(id));
        record.setContext(result);
        record.setProcessingSituation(processingSituation);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        recordService.save(record);
    }

}
