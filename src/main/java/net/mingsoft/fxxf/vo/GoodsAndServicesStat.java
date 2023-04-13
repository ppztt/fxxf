package net.mingsoft.fxxf.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yrg
 * @Description: 商品、服务类别统计
 **/
@Data
@ApiModel(value = "GoodsAndServicesStat")
public class GoodsAndServicesStat {

    @ApiModelProperty(value = "商品或服务类型")
    private String servicetype; // 商品类、服务类
    @ApiModelProperty(value = "类别名称")
    private String typename;    // 名称
    private Integer sid;        // 商品、服务 id
    @ApiModelProperty(value = "放心消费承诺单位数量")
    private Integer fxxf;   // 放心消费
    @ApiModelProperty(value = "线下无理由退货承诺店数量")
    private Integer wlyxf;  // 无理由消费
    
}
