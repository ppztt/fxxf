package net.mingsoft.fxxf.service.impl;


import net.mingsoft.fxxf.bean.vo.GoodsAndServicesStat;
import net.mingsoft.fxxf.mapper.GoodsAndServicesStatMapper;
import net.mingsoft.fxxf.service.GoodsAndServicesStatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商品服务类型统计
 */
@Service
public class GoodsAndServicesStatServiceImpl implements GoodsAndServicesStatService {

    @Resource
    private GoodsAndServicesStatMapper goodsAndServicesStatMapper;

    public ArrayList<GoodsAndServicesStat> list(String city, String district, String startTime, String endTime) {

        ArrayList<GoodsAndServicesStat> result = new ArrayList<>();
        List<Map> list = goodsAndServicesStatMapper.list2(city, district, startTime, endTime);

        List<Map> listType = goodsAndServicesStatMapper.listType();

        boolean isDistrict = StringUtils.isNotBlank(district);

        for (int i = 0; i < listType.size(); i++) {
            Map map = listType.get(i);
            String servicetype = map.get("servicetype").toString();
            String id = map.get("id").toString();
            String typename = map.get("typename").toString();

            int fxxf = 0;
            int wlyxf = 0;
            for (int j = 0; j < list.size(); j++) {
                Map listmap = list.get(j);
                Object typenameList = listmap.get("typename");
                if (typenameList != null && typenameList.equals(typename)) {

                    if (isDistrict) {
                        String districts = listmap.get("district").toString();
                        String[] c = districts.split(",");
                        Integer type = Integer.parseInt(listmap.get("type").toString());
                        if (type == 1) {
                            fxxf += getStringToArrayCount(district, c);
                        } else if (type == 2) {
                            wlyxf += getStringToArrayCount(district, c);
                        }
                    } else {
                        String citys = listmap.get("city").toString();
                        String[] c = citys.split(",");
                        Integer type = Integer.parseInt(listmap.get("type").toString());
                        if (type == 1) {
                            fxxf += getStringToArrayCount(city, c);
                        } else if (type == 2) {
                            wlyxf += getStringToArrayCount(city, c);
                        }
                    }

                }
            }

            GoodsAndServicesStat goodsAndServicesStat = new GoodsAndServicesStat();
            goodsAndServicesStat.setFxxf(fxxf);
            goodsAndServicesStat.setWlyxf(wlyxf);
            goodsAndServicesStat.setServicetype(servicetype);
            goodsAndServicesStat.setTypename(typename);
            goodsAndServicesStat.setSid(Integer.parseInt(id));
            result.add(goodsAndServicesStat);
        }

        //return goodsAndServicesStatMapper.list(city, district, startTime, endTime);
        return result;
    }

    private static int getStringToArrayCount(String srt, String[] arrary) {
        int count = 0;

        for (int i = 0; i < arrary.length; i++) {
            if (arrary[i].contains(srt)) {
                count++;
            }
        }
        return count;
    }

}
