package net.mingsoft.fxxf.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.bean.vo.ApiResult;
import net.mingsoft.fxxf.bean.vo.GoodsAndServicesStat;
import net.mingsoft.fxxf.service.GoodsAndServicesStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品服务类型统计
 */
@RestController
@Slf4j
@Api(tags = "商品服务类型统计")
@RequestMapping("/typestat")
public class GoodsAndServicesStatController {

    @Autowired
    private GoodsAndServicesStatService goodsAndServicesStatService;

    // @RequiresPermissions("manage:typestat")
    @ApiOperation(value = "商品服务类型统计列表")
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "city", value = "地市", dataType = "string", example = "广州"),
            @ApiImplicitParam(name = "district", value = "区县", dataType = "string", example = "天河"),
            @ApiImplicitParam(name = "startTime", value = "开始时间（格式：2020-04-01）"),
            @ApiImplicitParam(name = "endTime", value = "结束时间（格式：2020-04-01）"),
    })
//    @OperatorLogAnno(operType="查询", operModul="商品服务类型统计", operDesc="商品服务类型统计列表")
    public ApiResult<List<GoodsAndServicesStat>> list(@RequestParam(name = "city", required = false) String city,
                                                      @RequestParam(name = "district", required = false) String district,
                                                      @RequestParam(name = "startTime", required = false) String startTime,
                                                      @RequestParam(name = "endTime", required = false) String endTime
                          ) {
        log.info("商品服务类型统计列表参数：city:{}, district:{}, startTime:{}", city, district, startTime);
        List<GoodsAndServicesStat> list = goodsAndServicesStatService.list(city, district, startTime, endTime);
        return ApiResult.success(list);
    }

}
