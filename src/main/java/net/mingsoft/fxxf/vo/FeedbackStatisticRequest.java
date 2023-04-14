package net.mingsoft.fxxf.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "监督投诉请求参数实体")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class FeedbackStatisticRequest implements Serializable {

    @ApiModelProperty(name = "type", value = "单位类型 1、放心消费承诺单位 2、无理由退货单位", dataType = "int", example = "1")
    private Integer type = 1;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(name = "startTime", value = "开始时间")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(name = "endTime", value = "结束时间")
    private Date endTime;

}
