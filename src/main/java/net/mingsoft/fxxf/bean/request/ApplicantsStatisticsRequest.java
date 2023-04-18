package net.mingsoft.fxxf.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "ApplicantsStatisticsRequest", description = "经营者企业统计请求实体")
public class ApplicantsStatisticsRequest implements Serializable {

    @ApiModelProperty(name = "type", value = "单位类型 1、放心消费承诺单位 2、无理由退货单位", dataType = "int", example = "1", required = true)
    private Integer type = 1;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

}
