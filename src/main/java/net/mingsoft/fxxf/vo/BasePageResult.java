package net.mingsoft.fxxf.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author laijunbao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasePageResult<T extends Serializable> implements Serializable {

    @ApiModelProperty(value = "当前页码")
    private long current;

    @ApiModelProperty(value = "每页记录数")
    private long size;

    @ApiModelProperty(value = "总页数")
    private long pages;

    @ApiModelProperty(value = "总记录数")
    private long total;

    @ApiModelProperty(value = "列表")
    private List<T> records;


}
