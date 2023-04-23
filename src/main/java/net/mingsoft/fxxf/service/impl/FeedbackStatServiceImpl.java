package net.mingsoft.fxxf.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.bean.vo.ManagerInfoVo;
import net.mingsoft.fxxf.mapper.FeedbackStatMapper;
import net.mingsoft.fxxf.bean.entity.FeedbackStat;
import net.mingsoft.fxxf.service.FeedbackStatService;
import net.mingsoft.fxxf.service.ManagerInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Service
public class FeedbackStatServiceImpl implements FeedbackStatService {

    @Resource
    FeedbackStatMapper statMapper;
    @Resource
    private ManagerInfoService managerInfoService;

    @Override
    public Page statList(FeedbackStat feedback) {
        Page page = new Page<>(feedback.getCurrent(), feedback.getSize());
        List<FeedbackStat> statList = statMapper.statList(feedback, page);
        page.setRecords(statList);
        return page;
    }

    @Override
    public List<FeedbackStat> exportTable(FeedbackStat feedback) {
        List<FeedbackStat> statList = statMapper.statList(feedback, null);
        return statList;
    }

    @Override
    public Page statListByUserRole(FeedbackStat feedback, Page page) {
        List<FeedbackStat> statList;

        //根据角色id选择统计维度
        ManagerInfoVo user = managerInfoService.getLoginUserInfo();
        String city = user.getCity();
        Integer roleId = user.getRoleIds();
        feedback.setRoleId(roleId);

        //测试环境参数配置
        /*Integer roleId = 1;
        String city = "广州市"*/
        ;

        if (roleId == 1) {
            //系统管理员
            statList = statMapper.statListByAdminRole(feedback, null);
        } else {
            //地市管理员
            if (StringUtils.isEmpty(city)) {
                log.info("当前登录用户归属地市为Null，不执行查询;返回空集合");
                statList = Lists.newArrayList();
            } else {
                feedback.setCity(city);
                statList = statMapper.statListByCityRole(feedback, null);
            }
        }

        page.setRecords(statList);
        return page;
    }

    @Override
    public List<FeedbackStat> exportStatListByUserRole(FeedbackStat feedback) {
        List<FeedbackStat> statList;

        //根据角色id选择统计维度
        ManagerInfoVo user = managerInfoService.getLoginUserInfo();
        String city = user.getCity();
        Integer roleId = user.getRoleIds();
        feedback.setCity(city);
        feedback.setRoleId(roleId);

        //测试数据
        /*Integer roleId = 1;
        feedback.setCity("湛江市");*/

        if (roleId == 1) {
            //系统管理员
            statList = statMapper.statListByAdminRole(feedback, null);
        } else {
            //地市管理员
            statList = statMapper.statListByCityRole(feedback, null);
        }
        return statList;
    }
}

