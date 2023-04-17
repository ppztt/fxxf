package net.mingsoft.fxxf.bean.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 经营者企业信息录入请求实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "EnterpriseNewApplyRequest", description = "经营者企业信息录入请求实体")
public class EnterpriseNewApplyRequest implements Serializable {

    @ApiModelProperty(name = "type", value = "单位类型 1、放心消费承诺单位 2、无理由退货单位", dataType = "int", example = "1")
    private Integer type;

    /**
     * 经营者注册名称
     */
    @ApiModelProperty(value = "经营者注册名称")
    private String regName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;

    /**
     * 门店名称
     */
    @ApiModelProperty(value = "门店名称")
    private String storeName;

    /**
     * 所在市
     */
    @ApiModelProperty(value = "地市")
    private String city;

    /**
     * 所在区县
     */
    @ApiModelProperty(value = "区县")
    private String district;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    private String address;

    /**
     * 网店名称
     */
    @ApiModelProperty(value = "网店名称")
    private String onlineName;

    /**
     * 所属平台
     */
    @ApiModelProperty(value = "所属平台")
    private String platform;

    /**
     * 经营类别
     */
    @ApiModelProperty(value = "经营类别")
    private String management;

    /**
     * 类别明细
     */
    @ApiModelProperty(value = "类别明细")
    private List<String> details;

    /**
     * 负责人姓名
     */
    @ApiModelProperty(value = "负责人姓名")
    private String principal;

    /**
     * 负责人电话
     */
    @ApiModelProperty(value = "负责人电话")
    private String principalTel;

    /**
     * 品质保证或适用用品 承诺事项及内容
     */
    @ApiModelProperty(value = "品质保证")
    private String contents1;

    /**
     * 诚信保证或退货期限 承诺事项及内容
     */
    @ApiModelProperty(value = "诚信保证")
    private String contents2;

    /**
     * 维权保证或退货约定 承诺事项及内容
     */
    @ApiModelProperty(value = "维权保证")
    private String contents3;

    /**
     * 其他承诺事项及具体内容
     */
    @ApiModelProperty(value = "其他承诺事项及具体内容")
    private String contents4;

    /**
     * 经营场所-多个详细地址
     */
    @ApiModelProperty(value = "经营场所-多个详细地址")
    @TableField(exist = false)
    private String addrs;

    /**
     * 本单位申请日期 --> 企业申请日期
     */
    @ApiModelProperty(value = "企业申请日期")
    private Date applicationDate;

}
