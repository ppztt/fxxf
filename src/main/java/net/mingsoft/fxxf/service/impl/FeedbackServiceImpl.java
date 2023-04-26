package net.mingsoft.fxxf.service.impl;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.bean.base.BasePageResult;
import net.mingsoft.fxxf.bean.dto.ApplicantsStatisticsDto;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.bean.entity.Feedback;
import net.mingsoft.fxxf.bean.entity.FeedbackStat;
import net.mingsoft.fxxf.bean.entity.Region;
import net.mingsoft.fxxf.bean.enums.RoleTypeEnum;
import net.mingsoft.fxxf.bean.request.FeedBackCompanyPageRequest;
import net.mingsoft.fxxf.bean.request.FeedbackStatisticRequest;
import net.mingsoft.fxxf.bean.vo.FeedbackComplaintVo;
import net.mingsoft.fxxf.bean.vo.ManagerInfoVo;
import net.mingsoft.fxxf.mapper.ApplicantsMapper;
import net.mingsoft.fxxf.mapper.FeedbackMapper;
import net.mingsoft.fxxf.mapper.FeedbackStatMapper;
import net.mingsoft.fxxf.mapper.RegionMapper;
import net.mingsoft.fxxf.service.FeedbackService;
import net.mingsoft.fxxf.service.ManagerInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

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

    @Resource
    private ManagerInfoService managerInfoService;

    @Resource
    FeedbackMapper feedbackMapper;

    @Resource
    FeedbackStatMapper feedbackStatMapper;

    @Resource
    RegionMapper regionMapper;

    /**
     * 询企业投诉数量和已处理数量统计数据
     */
    @Override
    public BasePageResult<FeedbackComplaintVo> countByApplicantList(FeedBackCompanyPageRequest feedBackCompanyPageRequest) {
        ManagerInfoVo loginUserInfo = managerInfoService.getLoginUserInfo();
        Map<String, Object> map = new HashMap<>();

        map.put("roleId", loginUserInfo.getRoleIds());
        map.put("city", loginUserInfo.getCity());
        map.put("district", loginUserInfo.getDistrict());
        map.put("type", feedBackCompanyPageRequest.getType());
        map.put("search", feedBackCompanyPageRequest.getSearch());
        IPage<FeedbackComplaintVo> feedbackIPage = feedbackMapper.feedbackList(new Page<>(
                feedBackCompanyPageRequest.getCurrent(), feedBackCompanyPageRequest.getSize()), map);

        return new BasePageResult<>(feedbackIPage.getCurrent(), feedbackIPage.getSize(), feedbackIPage.getPages(),
                feedbackIPage.getTotal(), feedbackIPage.getRecords());
    }

    /**
     * 监督投诉统计
     */
    @Override
    public List<FeedbackStat> statistic(FeedbackStatisticRequest feedbackStatisticRequest) {
        List<FeedbackStat> statList;
        //根据角色id选择统计维度
        ManagerInfoVo loginUserInfo = managerInfoService.getLoginUserInfo();
        int roleId = loginUserInfo.getRoleIds();

        FeedbackStat feedback = new FeedbackStat();
        feedback.setStartTime(DateUtil.format(feedbackStatisticRequest.getStartTime(), "yyyy-MM-dd"));
        feedback.setEndTime(DateUtil.format(feedbackStatisticRequest.getEndTime(), "yyyy-MM-dd"));
        feedback.setType(feedbackStatisticRequest.getType().toString());
        feedback.setRoleId(roleId);
        if (roleId == 1) {
            //系统管理员
            statList = feedbackStatMapper.statListByAdminRole(feedback, null);
        } else {
            //地市管理员
            if (StringUtils.isEmpty(loginUserInfo.getCity())) {
                log.info("当前登录用户归属地市为Null，不执行查询;返回空集合");
                statList = Lists.newArrayList();
            } else {
                feedback.setCity(loginUserInfo.getCity());
                statList = feedbackStatMapper.statListByCityRole(feedback, null);
            }
        }
        return statList;
    }

    @Override
    public ArrayList<Applicants> companyList(String keyword) {
        ArrayList<Applicants> applicantsList = applicantsMapper.companyList(keyword);
        return applicantsList;
    }

    /**
     * 监督投诉统计优化
     */
    @Override
    public List<FeedbackStat> statisticChange(FeedbackStatisticRequest feedbackStatisticRequest) {
        //根据角色id选择统计维度
        ManagerInfoVo loginUserInfo = managerInfoService.getLoginUserInfo();
        Integer roleId = loginUserInfo.getRoleIds();
        if (!RoleTypeEnum.PROVINCE.getCode().equals(roleId) && StringUtils.isBlank(loginUserInfo.getCity())) {
            log.info("当前登录用户归属地市为Null，不执行查询;返回空集合");
            return new ArrayList<>();
        }

        String startTime = DateUtil.format(feedbackStatisticRequest.getStartTime(), "yyyy-MM-dd");
        String endTime = DateUtil.format(feedbackStatisticRequest.getEndTime(), "yyyy-MM-dd");
        Integer type = feedbackStatisticRequest.getType();
        FeedbackStat feedback = FeedbackStat.builder().startTime(startTime).endTime(endTime).type(type.toString())
                .city(loginUserInfo.getCity()).roleId(roleId).build();


        String roleRegIdentify = RoleTypeEnum.PROVINCE.getCode().equals(roleId) ? "city" : "district";
        ApplicantsStatisticsDto applicantsStatisticsDto = ApplicantsStatisticsDto.builder().startTime(startTime).endTime(endTime)
                .type(type).roleId(roleId).roleRegIdentify(roleRegIdentify).city(loginUserInfo.getCity()).build();

        // 统计经营者企业总数 和摘牌企业数
        ArrayList<ApplicantsStatisticsDto> applicantsStatisticsDtos = applicantsMapper.applicantsTotalTakeOffStatistics(applicantsStatisticsDto);

        String curRoleRegionName = RoleTypeEnum.PROVINCE.getCode().equals(roleId) ? loginUserInfo.getProvince() : loginUserInfo.getCity();
        // 下级区域
        Map<String, Integer> regionNameSortMap = regionMapper.underRegListInfoByCurName(curRoleRegionName).stream()
                .collect(Collectors.toMap(Region::getName, Region::getSort));


        List<FeedbackStat> feedbackStatistics = feedbackStatMapper.feedbackRegionStatisticByRole(feedback, roleRegIdentify);

        return feedbackStatistics.stream().filter(a -> {
            try {
                Field s = a.getClass().getDeclaredField(roleRegIdentify);
                s.setAccessible(Boolean.TRUE);
                if (StringUtils.isNotBlank((String) s.get(a))) {
                    return true;
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            return false;
        }).peek(fs -> {
            String regValue = null;
            try {
                Field f = fs.getClass().getDeclaredField(roleRegIdentify);
                f.setAccessible(Boolean.TRUE);
                regValue = (String) f.get(fs);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            String finalRegValue = regValue;
            fs.setSorted(regionNameSortMap.get(regValue));
            fs.setCompanyTotal(applicantsStatisticsDtos.stream().filter(a -> {
                try {
                    Field f = a.getClass().getDeclaredField(roleRegIdentify);
                    f.setAccessible(Boolean.TRUE);
                    String s = (String) f.get(a);
                    if (finalRegValue.equals(s)) {
                        return true;
                    }
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }).mapToInt(ApplicantsStatisticsDto::getCompanyTotal).sum());


            fs.setTakeOff(applicantsStatisticsDtos.stream().filter(a -> {
                try {
                    Field f = a.getClass().getDeclaredField(roleRegIdentify);
                    f.setAccessible(Boolean.TRUE);
                    String s = (String) f.get(a);
                    if (finalRegValue.equals(s)) {
                        return true;
                    }
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }).mapToInt(ApplicantsStatisticsDto::getTakeOff).sum());
            // 统计 被反馈单位数量、监督投诉的总条数、处理结果（待处理、督促告诫、投诉问题不存在、其他数）

        }).sorted(Comparator.comparing(FeedbackStat::getSorted)).collect(Collectors.toList());
    }


    public boolean ifcontainString(Map<Object, Object> map, String str) {
        boolean b = false;
        for (Object o : map.keySet()) {
            if (map.get(o).equals(str)) {
                return true;
            }
        }
        return b;
    }
}
