package net.mingsoft.fxxf.mapper;


import net.mingsoft.fxxf.vo.GoodsAndServicesStat;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商品服务类型统计
 */
public interface GoodsAndServicesStatMapper {

    public List<GoodsAndServicesStat> list(String city, String district, String startTime, String endTime);


    public List<Map> listType();

    public List<Map> list2(@Param("city") String city, @Param("district") String district, @Param("startTime") String startTime, @Param("endTime") String endTime);


}
