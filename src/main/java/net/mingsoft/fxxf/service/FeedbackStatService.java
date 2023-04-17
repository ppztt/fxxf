package net.mingsoft.fxxf.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.mingsoft.fxxf.bean.entity.FeedbackStat;

import java.util.List;

/**
 * @ClassName: FeedbackStatService
 * @Description 留言反馈统计
 * @Author Ligy
 * @Date 2020/2/10 15:41
 **/
public interface FeedbackStatService {

    Page statList(FeedbackStat feedback);

    List<FeedbackStat> exportTable(FeedbackStat feedback);

    Page statListByUserRole(FeedbackStat feedback, Page page);

    List<FeedbackStat> exportStatListByUserRole(FeedbackStat feedback);
}
