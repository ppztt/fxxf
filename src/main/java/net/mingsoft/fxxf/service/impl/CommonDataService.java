package net.mingsoft.fxxf.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.bean.entity.Region;
import net.mingsoft.fxxf.service.RegionService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author laijunbao
 */
@Component
@Slf4j
public class CommonDataService implements ApplicationRunner {

    private static Set<String> citys = new HashSet<>();
    private static Set<String> areas = new HashSet<>();
    private static Set<String> gzareas = new HashSet<>();
    private static Set<String> managements = new HashSet<>();
    private static Set<String> detailss = new HashSet<>();

    @Resource
    private RegionService regionService;

    static {
        managements.add("商品类");
        managements.add("服务类");
        managements.add("商品及服务类");

        detailss.add("家用电子电器类");
        detailss.add("服装鞋帽类");
        detailss.add("食品类");
        detailss.add("烟、酒、饮料类");
        detailss.add("房屋及建材类");
        detailss.add("日用商品类");
        detailss.add("首饰及文体用品类");
        detailss.add("医药及医疗用品类");
        detailss.add("交通工具类");
        detailss.add("农用生产资料类");
        detailss.add("生活、社会服务类");
        detailss.add("房屋装修及物业服务类");
        detailss.add("旅游服务");
        detailss.add("文化、娱乐、体育服务");
        detailss.add("邮政业服务");
        detailss.add("电信服务");
        detailss.add("互联网服务");
        detailss.add("金融服务");
        detailss.add("保险服务");
        detailss.add("卫生保健服务");
        detailss.add("教育培训服务");
        detailss.add("公共设施服务");
        detailss.add("销售服务");
        detailss.add("其他商品和服务");
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("获取静态数据");
            List<Region> regionsByCitys = regionService.list(new QueryWrapper<Region>().eq("level", 2).groupBy("name,id,code,short_name,pcode,lng,lat,sort,create_time,update_time,memo,status,tenant_code,level"));
            List<Region> regionsByAreas = regionService.list(new QueryWrapper<Region>().eq("level", 3).groupBy("name,id,code,short_name,pcode,lng,lat,sort,create_time,update_time,memo,status,tenant_code,level"));

            regionsByCitys.forEach(r -> citys.add(r.getName()));
            regionsByAreas.forEach(r -> areas.add(r.getName()));
            regionsByAreas.stream().filter(r -> Objects.equals(r.getPcode(), "440100")).forEach(r -> gzareas.add(r.getName()));
        } catch (Exception e) {
            log.error("获取静态数据出错");
        }
    }

    public static boolean isAccessCity(String city) {
        return citys.contains(city);
    }

    public static boolean isAccessArea(String area) {
        return areas.contains(area);
    }

    public static boolean isAccessManagements(String management) {
        return managements.contains(management);
    }

    public static Set<String> getAreas() {
        return gzareas;
    }

    public static Set<String> getManagements() {
        return managements;
    }

    public static Set<String> getDetailss() {
        return detailss;
    }

    public static boolean isAccessDetails(String details) {
        boolean isExist = true;
        for (String s : details.split(",")) {
            if (!detailss.contains(s)) {
                isExist = false;
                break;
            }
        }
        return isExist;
    }
}
