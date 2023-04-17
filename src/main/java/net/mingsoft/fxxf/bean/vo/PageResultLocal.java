package net.mingsoft.fxxf.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author laijunbao
 */
@Data
public class PageResultLocal<T> implements Serializable {

    @ApiModelProperty(value = "当前页码")
    private Integer current;

    @ApiModelProperty(value = "每页记录数")
    private Integer size;

    @ApiModelProperty(value = "总页数")
    private Integer pages;

    @ApiModelProperty(value = "总记录数")
    private Integer total;

    @ApiModelProperty(value = "列表")
    private T records;


}
