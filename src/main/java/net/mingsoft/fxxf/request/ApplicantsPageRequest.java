package net.mingsoft.fxxf.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 经营者条件分页查询请求vo
 *
 * @author: huangjunjian
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel("经营者条件分页查询请求vo")
public class ApplicantsPageRequest  extends BasePageRequest implements Serializable {

    private static final long serialVersionUID = 6887611197913735614L;

    @ApiModelProperty(value = "申报单位类型(1:放心消费单位； 2:无理由退货实体店)", required = true)
    private String type;

    @ApiModelProperty(value = "经营场所-所在市")
    private String city;

    @ApiModelProperty(value = "经营场所-所在区县")
    private String district;
    @ApiModelProperty(value = "经营场所-所在镇")
    private String town;
    @ApiModelProperty(value = "'状态(1:在期； 0:摘牌；2:过期; 4:待审核; 5:县级审核通过; 6:市级审核通过; 7:审核不通过 )")
    private String status;
    @ApiModelProperty(value = "有效起始时间（审核通过时间）")
    private String startTime;
    @ApiModelProperty(value = "有效截止时间（审核通过时间）")
    private String endTime;
    @ApiModelProperty(value = "关键字")
    private String search;

    @ApiModelProperty(value = "经营类别")
    private String management;
    @ApiModelProperty(value = "类别明细")
    private String details;
}
