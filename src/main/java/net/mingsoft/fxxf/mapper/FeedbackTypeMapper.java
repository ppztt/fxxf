package net.mingsoft.fxxf.mapper;


import net.mingsoft.fxxf.bean.entity.FeedbackType;

import java.util.List;

public interface FeedbackTypeMapper {

    List<FeedbackType> feedbackType(Integer flag);

    List<FeedbackType> feedbackReason(Integer id);
}
