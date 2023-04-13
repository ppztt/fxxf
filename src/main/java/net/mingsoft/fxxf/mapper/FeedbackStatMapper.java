package net.mingsoft.fxxf.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.mingsoft.fxxf.entity.FeedbackStat;

import java.util.List;

/**
 * @author Ligy
 * @description 留言反馈统计
 * @date 2020/2/10 15:40
 **/
public interface FeedbackStatMapper {

    List<FeedbackStat> statList(FeedbackStat feedback, Page page);

    List<FeedbackStat> statListByAdminRole(FeedbackStat feedback, Page page);

    List<FeedbackStat> statListByCityRole(FeedbackStat feedback, Page page);
}
