package net.mingsoft.fxxf.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import net.mingsoft.fxxf.bean.entity.Feedback;
import net.mingsoft.fxxf.bean.vo.FeedbackCompanyDetailsVo;
import net.mingsoft.fxxf.bean.vo.FeedbackComplaintVo;
import net.mingsoft.fxxf.bean.vo.FeedbackMsgVo;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 留言反馈 Mapper 接口
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-09
 */
public interface FeedbackMapper extends BaseMapper<Feedback> {

    IPage<FeedbackComplaintVo> feedbackList(IPage<T> page, @Param("map") Map<String, Object> map);

    List<FeedbackCompanyDetailsVo> feedbackCompanyDetails(@Param("applicantsId") String applicantsId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    FeedbackMsgVo getMsgCnt(@Param("roleId") Integer roleId, @Param("city") String city, @Param("district") String district);

}
