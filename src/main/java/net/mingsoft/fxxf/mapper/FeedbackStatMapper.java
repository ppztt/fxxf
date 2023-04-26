package net.mingsoft.fxxf.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.mingsoft.fxxf.bean.entity.FeedbackStat;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Ligy
 * @description 留言反馈统计
 * @date 2020/2/10 15:40
 **/
public interface FeedbackStatMapper {

    List<FeedbackStat> statList(@Param("feedback") FeedbackStat feedback, Page page);

    List<FeedbackStat> statListByAdminRole(@Param("feedback") FeedbackStat feedback, Page page);

    List<FeedbackStat> statListByCityRole(@Param("feedback") FeedbackStat feedback, Page page);


    //
    List<FeedbackStat> feedbackRegionStatisticByRole(@Param("feedback") FeedbackStat feedback,@Param("roleRegIdentify") String roleRegIdentify);
}
