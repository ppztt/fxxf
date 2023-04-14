package net.mingsoft.fxxf.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author laijunbao
 */
@Data
@ApiModel(value = "ApplicantsUnitParamsVo")
public class ApplicantsUnitParamsVo2 implements Serializable {

    @ApiModelProperty(value = "申报单位id")
    private Integer id;

    /**
     * 经营者注册名称
     */
    @ApiModelProperty(value = "经营者注册名称")
    @NotBlank(message = "请输入经营者注册名称")
    private String regName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;

    /**
     * 经营场所-所在市
     */
    @ApiModelProperty(value = "经营场所-所在市")
    private String city;

    /**
     * 经营场所-所在区县
     */
    @ApiModelProperty(value = "经营场所-所在区县")
    private String district;

    /**
     * 经营场所-所在镇
     */
    @ApiModelProperty(value = "经营场所-所在镇")
    private String town;

    /**
     * 经营场所-详细地址
     */
    @ApiModelProperty(value = "经营场所-详细地址")
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
     * 网店链接
     */
    @ApiModelProperty(value = "网店链接")
    private String onlineLink;

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
     * 门店名称
     */
    @ApiModelProperty(value = "门店名称")
    private String storeName;

    /**
     * 有效起始时间（审核通过时间）
     */
    @ApiModelProperty(value = "有效起始时间（审核通过时间）")
    @NotNull(message = "请输入有效起始时间")
    private String startTime;

    /**
     * 有效截止时间
     */
    @ApiModelProperty(value = "有效截止时间")
    @NotNull(message = "请输入有效截止时间")
    private String endTime;

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
     * 联系人姓名
     */
    @ApiModelProperty(value = "联系人姓名")
    private String contacts;

    /**
     * 联系人电话
     */
    @ApiModelProperty(value = "联系人电话")
    private String contactsTel;

    /**
     * 加入的行业协会
     */
    @ApiModelProperty(value = "加入的行业协会")
    private String joinIndustry;

    /**
     * 是否连续承诺
     */
    @ApiModelProperty(value = "是否连续承诺")
    private String contCommitment;

    /**
     * 连续承诺次数
     */
    @ApiModelProperty(value = "连续承诺次数")
    private Integer commNum;

    /**
     * 品质保证 承诺事项及内容
     */
    @ApiModelProperty(value = "品质保证")
    private String contents1;

    /**
     * 诚信保证 承诺事项及内容
     */
    @ApiModelProperty(value = "诚信保证")
    private String contents2;

    /**
     * 维权保证 承诺事项及内容
     */
    @ApiModelProperty(value = "维权保证")
    private String contents3;

    /**
     * 其他承诺事项及具体内容
     */
    @ApiModelProperty(value = "其他承诺事项及具体内容")
    private String contents4;

    /**
     * 本单位声明内容
     */
    @ApiModelProperty(value = "本单位声明内容")
    private String statement;

    /**
     * 本单位申请日期 --> 企业申请日期
     */
    @ApiModelProperty(value = "企业申请日期")
    private LocalDateTime applicationDate;

    /**
     * 行业协会意见内容
     */
    @ApiModelProperty(value = "行业协会意见内容")
    private String industryContent;

    /**
     * 行业协会意见日期
     */
    @ApiModelProperty(value = "行业协会意见日期")
    private LocalDateTime industryDate;

    /**
     * 行业协会意见单位名称
     */
    @ApiModelProperty(value = "行业协会意见单位名称")
    private String industryCompanyName;

    /**
     * 行业协会意见审核人员
     */
    @ApiModelProperty(value = "行业协会意见审核人员")
    private String industryAuditors;

    /**
     * 消委会意见内容
     */
    @ApiModelProperty(value = "消委会意见内容")
    private String ccContent;

    /**
     * 消委会意见日期
     */
    @ApiModelProperty(value = "消委会意见日期")
    private LocalDateTime ccDate;

    /**
     * 消委会单位名称
     */
    @ApiModelProperty(value = "消委会单位名称")
    private String ccCompanyName;

    /**
     * 消委会审核人员
     */
    @ApiModelProperty(value = "消委会审核人员")
    private String ccAuditors;

    /**
     * 状态(1:在期； 0:摘牌)
     */
    @ApiModelProperty(value = "状态(1:在期； 0:摘牌；2:过期)")
    private Integer status;

    /**
     * 摘牌时间
     */
    @ApiModelProperty(value = "摘牌时间")
    private LocalDateTime delTime;

    /**
     * 摘牌具体原因
     */
    @ApiModelProperty(value = "摘牌具体原因")
    private String delReason;

    /**
     * 摘牌其它必要信息
     */
    @ApiModelProperty(value = "摘牌其它必要信息")
    private String delOther;

    /**
     * 增加其他承诺的单位数量
     */
    @ApiModelProperty(value = "增加其他承诺的单位数量")
    private Integer addContents1Cnt;

    /**
     * 经营场所-多个详细地址
     */
    @ApiModelProperty(value = "经营场所-多个详细地址")
    @TableField(exist = false)
    private String addrs;

    public void setCreditCode(String creditCode) {
        if(!Objects.isNull(creditCode)){
            this.creditCode = creditCode.trim();
        }
    }

}
