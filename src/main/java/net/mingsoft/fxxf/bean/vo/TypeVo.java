package net.mingsoft.fxxf.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "TypeVo")
public class TypeVo implements Serializable {

    /**
     * 商品类
     */
    @ApiModelProperty(value = "商品类")
    private List<String> commodities;

    /**
     * 服务类
     */
    @ApiModelProperty(value = "服务类")
    private List<String> services;
}
