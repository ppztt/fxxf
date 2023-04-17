package net.mingsoft.fxxf.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: yrg
 * @Date: 2020-01-14 9:20
 * @Description: 地区
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionVo {

    private String code;
    private String pcode;
    private String name;
    private List<RegionVo> children;

}
