package net.mingsoft.fxxf.service;


import net.mingsoft.fxxf.bean.vo.GoodsAndServicesStat;

import java.util.ArrayList;

/**
 * 商品服务类型统计
 */
public interface GoodsAndServicesStatService {

    ArrayList<GoodsAndServicesStat> list(String city, String district, String startTime, String endTime);
}
