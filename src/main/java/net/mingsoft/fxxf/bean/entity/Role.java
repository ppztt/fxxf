package net.mingsoft.fxxf.bean.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-09
 */
@TableName("sys_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@ApiModel("角色参数")
public class Role extends Model<Role> {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("角色id")
    private Integer id;

    /**
     * 名称
     */
    @NotBlank(message = "请输入角色名称")
    @ApiModelProperty("角色名称")
    private String name;

    /**
     * 备注解释
     */
    @ApiModelProperty("备注")
    private String explation;

    /**
     * 是否可用,1：可用，0不可用
     */
    @ApiModelProperty("是否可用,1：可用，0不可用")
    private Integer available;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
