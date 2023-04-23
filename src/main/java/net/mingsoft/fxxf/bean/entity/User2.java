package net.mingsoft.fxxf.bean.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-09
 */
@TableName("sys_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@Setter
@Getter
public class User2 extends Model<User2> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "用户id；主键")
    private Integer id;

    /**
     * 用户名
     */
    @NotNull(message = "用户名必填")
    @ApiModelProperty(value = "用户名")
    private String account;

    /**
     * 密码
     */
    @NotNull(message = "密码必填")
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 新密码
     **/
    @TableField(exist = false)
    @ApiModelProperty(value = "新密码")
    private String newPassword;

    /**
     * 真实姓名
     */
    @NotNull(message = "真实姓名必填")
    @ApiModelProperty(value = "真实姓名（企业用户为：经营者注册名称 企业用户为：行业协会名称）", required = true)
    private String realname;

    /**
     * 电子邮件
     */
    @Email(message = "电子邮件格式不正确")
    @ApiModelProperty(value = "电子邮件")
    private String email;

    /**
     * 所在省
     */

    @ApiModelProperty(value = "省份")
    private String province;

    /**
     * 所在市
     */
    @NotNull(message = "地市必填")
    @ApiModelProperty(value = "地市", required = true)
    private String city;

    /**
     * 所在区县
     */
    @ApiModelProperty(value = "区县")
    private String district;

    /**
     * 所在镇
     */
    @ApiModelProperty(value = "乡镇")
    private String town;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    private String address;

    /**
     * 邮政编码
     */
    @ApiModelProperty(value = "邮政编码")
    private String zipcode;

    /**
     * 联系电话
     */
    @NotNull(message = "联系电话必填")
    @ApiModelProperty(value = "联系电话", required = true)
    private String phone;

    /**
     * 所属角色
     */
    @ApiModelProperty(value = "角色id")
    private Integer roleId;

    @TableField(exist = false)
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", required = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", required = false)
    private LocalDateTime updateTime;

    /**
     * 所属角色实体
     */
    @JsonIgnore
    @TableField(exist = false)
    private Role role;

    /**
     * 拥有的权限列表
     */
    @JsonIgnore
    @TableField(exist = false)
    private List<Permission> permissions;

    /**
     * 用户类型（1：后台用户； 2：企业用户 3: 行业协会）
     */
    @ApiModelProperty(value = "用户类型（1：后台用户； 2：企业用户; 3: 行业协会")
    private int usertype;

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
     * 经营类别
     */
    @ApiModelProperty(value = "经营类别")
    private String management;

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

//    /**
//     * 用户名（行业协会仅有）
//     */
//    @ApiModelProperty(value = "用户名（行业协会仅有）")
//    private String industryUserName;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
