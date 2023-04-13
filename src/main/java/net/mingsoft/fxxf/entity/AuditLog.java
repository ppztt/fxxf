package net.mingsoft.fxxf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * <p>
 * 审核操作记录表
 * </p>
 *
 * @author laijunbao
 * @since 2020-09-19
 */
@TableName("cc_auditlog")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@ApiModel("审核操作记录表")
public class AuditLog extends Model<AuditLog> {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("id")
    private Integer id;

    /**
     * 申请单位id
     */
    @ApiModelProperty("申请单位id")
    private Integer appId;

    /**
     * 审核时间
     */
    @ApiModelProperty("审核时间")
    private LocalDateTime createTime;

    /**
     * 审核填写内容信息
     */
    @ApiModelProperty("审核填写内容信息")
    private String contents;

    /**
     * 审核结果（1：通过； 0：不通过）
     */
    @ApiModelProperty("审核结果（1：通过； 0：不通过）")
    private Integer status;

    /**
     * 审核人员
     */
    @ApiModelProperty("审核人员")
    private Integer auditor;

    /**
     * 审核角色（关联取角色名称）
     */
    @ApiModelProperty("审核角色（关联取角色名称）")
    private Integer roleId;
}
