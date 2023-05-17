package net.mingsoft.fxxf.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.service.ApplicantsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author laijunbao
 */
@Slf4j
@Component
public class ApplicantsTask {

    @Autowired
    private ApplicantsService applicantsService;

    @Scheduled(cron = "${task.applicants}")
    @Transactional(rollbackFor = Exception.class)
    public void checkUnitTermOfValidity(){
        log.info("每天0点例行检查【放心消费单位】有效期程序启动。。。");
        List<Applicants> applicants = applicantsService.list(
                new QueryWrapper<Applicants>()
                        .lambda()
                        .select(Applicants::getId, Applicants::getEndTime)
                        .eq(Applicants::getType, 1)
                        .eq(Applicants::getStatus, 1));

        List<Integer> needUpdateIdList = applicants.stream().filter(item -> LocalDate.now().isAfter(item.getEndTime()))
                .map(Applicants::getId).collect(Collectors.toList());

        if (!needUpdateIdList.isEmpty()) {
            UpdateWrapper<Applicants> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .in(Applicants::getId, needUpdateIdList)
                    // 过渡期
                    .set(Applicants::getStatus, 3)
                    // .set(Applicants::getDelReason, "过期")
                    .set(Applicants::getDelTime, LocalDateTime.now())
                    .set(Applicants::getUpdateTime, LocalDateTime.now());

            applicantsService.update(updateWrapper);
        }
        log.info("每天0点例行检查【放心消费单位】有效期程序完成，共更新{}条记录。。。",needUpdateIdList.size());
    }

}

