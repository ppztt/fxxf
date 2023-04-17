package net.mingsoft.fxxf.service;


import net.mingsoft.fxxf.bean.vo.GoodsAndServicesStat;

import java.util.List;

/**
 * 商品服务类型统计
 */
public interface GoodsAndServicesStatService {

    public List<GoodsAndServicesStat> list(String city, String district, String startTime, String endTime);
}
