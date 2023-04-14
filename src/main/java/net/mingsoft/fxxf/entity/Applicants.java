package net.mingsoft.fxxf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import net.mingsoft.basic.entity.ManagerEntity;
import org.apache.shiro.SecurityUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 申报单位
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-09
 */
@TableName("cc_applicants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@ApiModel(value = "Applicants")
@Setter
@Getter
public class Applicants extends Model<Applicants> {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "申报单位id")
    private Integer id;

    /**
     * 申报单位类型(1:放心消费单位； 2:无理由退货实体店)
     */
    @ApiModelProperty(value = "申报单位类型(1:放心消费单位； 2:无理由退货实体店)")
    private Integer type;

    /**
     * 状态(1:在期； 0:摘牌)
     */
    @ApiModelProperty(value = "状态(1:在期； 0:摘牌；2:过期; 4:待审核; 5:县级审核通过; 6:(行业协会审核通过)市级审核通过; 7:审核不通过 8:行业协会审核不通过)")
    private Integer status;

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
     * 经营场所-所在省（目前只有广东省）
     */
    @ApiModelProperty(value = "经营场所-所在省（目前只有广东省）")
    private String province;

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
     * 经营场所-多个详细地址
     */
    @ApiModelProperty(value = "经营场所-多个详细地址")
    @TableField(exist = false)
    private String addrs;

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
    private String details;

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
     * 有效起始时间（审核通过时间）
     */
    @ApiModelProperty(value = "有效起始时间（审核通过时间）")
    @JsonFormat(pattern = "yyyy-MM",timezone = "GMT+8")
    private LocalDate startTime;

    /**
     * 有效截止时间
     */
    @ApiModelProperty(value = "有效截止时间")
    @JsonFormat(pattern = "yyyy-MM",timezone = "GMT+8")
    private LocalDate endTime;

    /**
     * 品质保证或适用用品 承诺事项及内容
     */
    @ApiModelProperty(value = "品质保证或适用商品 承诺事项及内容")
    private String contents1;

    /**
     * 诚信保证或退货期限 承诺事项及内容
     */
    @ApiModelProperty(value = "诚信保证或退货期限 承诺事项及内容")
    private String contents2;

    /**
     * 维权保证或退货约定 承诺事项及内容
     */
    @ApiModelProperty(value = "维权保证或退货约定 承诺事项及内容")
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
    @JsonIgnore
    private String statement;

    /**
     * 本单位申请日期
     */
    @ApiModelProperty(value = "本单位申请日期")
    @JsonIgnore
    private LocalDateTime applicationDate;

    /**
     * 行业协会意见内容
     */
    @ApiModelProperty(value = "行业协会意见内容")
    @JsonIgnore
    private String industryContent;

    /**
     * 行业协会意见日期
     */
    @ApiModelProperty(value = "行业协会意见日期")
    @JsonIgnore
    private LocalDateTime industryDate;

    /**
     * 行业协会意见单位名称
     */
    @ApiModelProperty(value = "行业协会意见单位名称")
    @JsonIgnore
    private String industryCompanyName;

    /**
     * 行业协会意见审核人员
     */
    @ApiModelProperty(value = "行业协会意见审核人员")
    @JsonIgnore
    private String industryAuditors;

    /**
     * 消委会意见内容
     */
    @ApiModelProperty(value = "消委会意见内容")
    @JsonIgnore
    private String ccContent;

    /**
     * 消委会意见日期
     */
    @ApiModelProperty(value = "消委会意见日期")
    @JsonIgnore
    private LocalDateTime ccDate;

    /**
     * 消委会单位名称
     */
    @ApiModelProperty(value = "消委会单位名称")
    @JsonIgnore
    private String ccCompanyName;

    /**
     * 消委会审核人员
     */
    @ApiModelProperty(value = "消委会审核人员")
    @JsonIgnore
    private String ccAuditors;

    /**
     * 摘牌时间
     */
    @ApiModelProperty(value = "摘牌时间")
    @JsonIgnore
    private LocalDateTime delTime;

    /**
     * 摘牌具体原因
     */
    @ApiModelProperty(value = "摘牌具体原因")
    @JsonIgnore
    private String delReason;

    /**
     * 摘牌其它必要信息
     */
    @ApiModelProperty(value = "摘牌其它必要信息")
    @JsonIgnore
    private String delOther;

    /**
     * 公示时间
     */
    @ApiModelProperty(value = "公示时间")
    @JsonIgnore
    private LocalDateTime pubTime;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间（最后操作时间）")
    private LocalDateTime updateTime;

    /**
     * 增加其他承诺的单位数量
     */
    @ApiModelProperty(value = "增加其他承诺的单位数量")
    private Integer addContents4Cnt;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private int creater;

    /**
     * 创建方式（企业提交； 区县导入； 地市导入）
     */
    @ApiModelProperty(value = "创建方式（企业提交； 区县导入； 地市导入）")
    private String createType;

    /**
     * 待审核的角色
     *
     */
    @ApiModelProperty(value = "待审核的角色id。当前登录用户的角色id小于这个id，并且当前状态在4（待审核）、 5（县级审核通过）、6（市级审核通过（改为行业协会））之一，说明可以审核")
    private int auditRoleId;

    /**
     * 是否待审(0:不是；1:是)
     */
    @ApiModelProperty(value = "是否待审(0:不是；1:是)")
    @TableField(exist = false)
    public Integer isNew;

    @ApiModelProperty(value = "创建人账号")
    @TableField(exist = false)
    private String account;

    public void setCreditCode(String creditCode) {
        if(!Objects.isNull(creditCode)){
            this.creditCode = creditCode.trim();
        }
    }

    public Integer getCommNum() {
        return commNum == null ? 0 : commNum;
    }

    public Integer getIsNew() {
        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();

       /* if(!Objects.equals(createType, "企业提交")){
            if(user.getRoleId() < auditRoleId && (Objects.equals(status, 4) || Objects.equals(status, 5) || Objects.equals(status, 6))){
                return 1;
            }
        }*/
        if(user != null && !Objects.isNull(user.getRoleId())){
            if(Objects.equals(status, 4)){
                return 1;
            }
            if(user.getRoleId() <= auditRoleId && (Objects.equals(status, 5) || Objects.equals(status, 6))){
                return 1;
            }
        }
        return 0;
    }
}
