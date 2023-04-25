package net.mingsoft.fxxf.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 经营者统计相关业务指标dto
 *
 * @author: huangjunjian
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantsStatisticsDto implements Serializable {

    @ApiModelProperty(value = "统计期限开始时间")
    private String startTime;

    @ApiModelProperty(value = "统计期限结束时间")
    private String endTime;

    @ApiModelProperty(value = "申报单位类型(1:放心消费单位； 2:无理由退货实体店)")
    private Integer type;

    @ApiModelProperty(value = "地市")
    private String city;

    @ApiModelProperty(value = "区县")
    private String district;

    @ApiModelProperty(value = "用户角色id")
    private Integer roleId;

    @ApiModelProperty(value = "根据角色按区域聚合统计标识 roleId =1使用city,以外使用district")
    private String roleRegIdentify;
    @ApiModelProperty(value = "承诺单位数量")
    private Integer companyTotal;

    @ApiModelProperty(value = "摘牌数量")
    private Integer takeOff;
}
