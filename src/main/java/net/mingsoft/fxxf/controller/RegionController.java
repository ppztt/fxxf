package net.mingsoft.fxxf.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.fxxf.service.ApplicantsService;
import net.mingsoft.fxxf.vo.RegionVo;
import net.mingsoft.utils.BeanUtil;
import net.mingsoft.utils.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: yrg
 * @Date: 2020-01-14 9:28
 * @Description: 地区接口
 **/
@Api(tags = "地区信息")
@RestController
public class RegionController {

    @Autowired
    private ApplicantsService applicantsService;

    @ApiOperation(value = "地区", notes = "获取广东省地区信息")
    @GetMapping("/gd-regin")
//    @OperatorLogAnno(operType = "查询", operModul = "地区信息", operDesc = "获取广东省地区信息")
    public ResponseBean gdRegin() {
        List<RegionVo> list = new ArrayList<>();
        List<RegionVo> gdRegion = applicantsService.getGdRegion();
        if (BeanUtil.isNotBlank(gdRegion)) {
            for (RegionVo regionVo : gdRegion) {
                if ("440000".equals(regionVo.getPcode())) {
                    regionVo.setChildren(getGdChildrenNode(regionVo.getCode(), gdRegion));
                    list.add(regionVo);
                }
            }
        }
        return new ResponseBean(list);
    }

    private List<RegionVo> getGdChildrenNode(String pcode, List<RegionVo> regionVos) {
        List<RegionVo> list = new ArrayList<>();
        for (RegionVo regionVo : regionVos) {
            if ("440000".equals(regionVo.getPcode())) {
                continue;
            }

            if (pcode.equals(regionVo.getPcode())) {
                regionVo.setChildren(getGdChildrenNode(regionVo.getCode(), regionVos));
                list.add(regionVo);
            }
        }
        return list;
    }


}
