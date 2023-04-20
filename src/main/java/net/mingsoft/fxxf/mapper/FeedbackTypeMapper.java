package net.mingsoft.fxxf.mapper;


import net.mingsoft.fxxf.bean.entity.FeedbackType;

import java.util.ArrayList;

public interface FeedbackTypeMapper {

    ArrayList<FeedbackType> feedbackType(Integer flag);

    ArrayList<FeedbackType> feedbackReason(Integer id);
}
