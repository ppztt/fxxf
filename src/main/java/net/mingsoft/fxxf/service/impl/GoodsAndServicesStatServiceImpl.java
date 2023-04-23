package net.mingsoft.fxxf.service.impl;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.bean.entity.Type;
import net.mingsoft.fxxf.bean.vo.GoodsAndServicesStat;
import net.mingsoft.fxxf.service.ApplicantsService;
import net.mingsoft.fxxf.service.GoodsAndServicesStatService;
import net.mingsoft.fxxf.service.TypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 商品服务类型统计
 */
@Service
public class GoodsAndServicesStatServiceImpl implements GoodsAndServicesStatService {

    @Resource
    private ApplicantsService applicantsService;
    @Resource
    private TypeService typeService;

    public ArrayList<GoodsAndServicesStat> list(String city, String district, String startTime, String endTime) {
        ArrayList<GoodsAndServicesStat> result = new ArrayList<>();
        List<Type> typeList = getTypeList();
        List<Applicants> applicantsList = getapplicantsList(city, district, startTime, endTime);
        for (Type type : typeList) {
            Integer typeId = type.getId();
            String typeName = type.getName();

            AtomicInteger fxxf = new AtomicInteger(0);
            AtomicInteger wlyxf = new AtomicInteger(0);
            for (Applicants applicants : applicantsList) {
                Integer applicantsType = applicants.getType();
                String applicantsCity = applicants.getCity();
                String applicantsDistrict = applicants.getDistrict();
                String details = applicants.getDetails();
                String[] detailArray = details.split(",");
                for (String detailItem : detailArray) {
                    if (!Objects.equals(detailItem, typeName)) {
                        continue;
                    }
                    if (StringUtils.isNotBlank(district)) {
                        // 按区县统计
                        statistic(fxxf, wlyxf, applicantsType, district, applicantsDistrict);
                    } else {
                        // 按市统计
                        statistic(fxxf, wlyxf, applicantsType, city, applicantsCity);
                    }
                }
            }
            GoodsAndServicesStat goodsAndServicesStat = new GoodsAndServicesStat();
            goodsAndServicesStat.setFxxf(fxxf.get());
            goodsAndServicesStat.setWlyxf(wlyxf.get());
            goodsAndServicesStat.setServicetype(type.getRemarks());
            goodsAndServicesStat.setTypename(typeName);
            goodsAndServicesStat.setSid(typeId);
            result.add(goodsAndServicesStat);
        }
        return result;
    }

    private void statistic(AtomicInteger fxxf, AtomicInteger wlyxf, Integer applicantsType, String region, String applicantsRegion) {
        String[] regions = applicantsRegion.split(",");
        int count = getStringToArrayCount(region, regions);
        if (applicantsType == 1) {
            fxxf.addAndGet(count);
        }
        if (applicantsType == 2) {
            wlyxf.addAndGet(count);
        }
    }

    private int getStringToArrayCount(String srt, String[] arrary) {
        int count = 0;
        for (String s : arrary) {
            if (s.contains(srt)) {
                count++;
            }
        }
        return count;
    }

    private List<Type> getTypeList() {
        return typeService.list(new QueryWrapper<Type>().lambda()
                .select(Type::getId, Type::getRemarks, Type::getName)
                .in(Type::getType, Arrays.asList(5, 6))
                .isNotNull(Type::getRemarks)
                .isNotNull(Type::getName));
    }

    private List<Applicants> getapplicantsList(String city, String district, String startTimeStr, String endTimeStr) {
        LambdaQueryWrapper<Applicants> queryWrapper = new QueryWrapper<Applicants>().lambda()
                .select(Applicants::getType, Applicants::getCity, Applicants::getDistrict, Applicants::getManagement, Applicants::getDetails)
                .isNotNull(Applicants::getDetails)
                .like(CharSequenceUtil.isNotBlank(city), Applicants::getCity, city)
                .like(CharSequenceUtil.isNotBlank(district), Applicants::getDistrict, district);
        if (CharSequenceUtil.isNotBlank(startTimeStr)) {
            DateTime startTime = DateUtil.beginOfDay(DateUtil.parseDate(startTimeStr));
            queryWrapper.ge(Applicants::getStartTime, startTime);
        }
        if (CharSequenceUtil.isNotBlank(endTimeStr)) {
            DateTime endTime = DateUtil.endOfDay(DateUtil.parseDate(endTimeStr));
            queryWrapper.le(Applicants::getStartTime, endTime);
        }
        return applicantsService.list(queryWrapper);
    }

}
