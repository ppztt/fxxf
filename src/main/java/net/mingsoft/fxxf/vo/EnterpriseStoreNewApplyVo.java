package net.mingsoft.fxxf.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 企业新申请无理由退货承诺vo
 */
@Data
@ApiModel(value = "EnterpriseStoreNewApplyVo", description = "企业新申请无理由退货承诺vo")
public class EnterpriseStoreNewApplyVo implements Serializable {

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
     * 适用商品
     */
    @ApiModelProperty(value = "适用商品")
    private String contents1;

    /**
     * 诚信保证或退货期限 承诺事项及内容
     */
    @ApiModelProperty(value = "退货期限")
    private String contents2;

    /**
     * 维权保证或退货约定 承诺事项及内容
     */
    @ApiModelProperty(value = "退货约定")
    private String contents3;

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
