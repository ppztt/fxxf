package net.mingsoft.fxxf.bean.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-17
 */
@TableName("sys_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@ApiModel(value = "Type")
public class Type extends Model<Type> {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Integer id;

    /**
     * 类型（1：反馈原因； 2：处理结果）
     */
    @ApiModelProperty(value = "类型（1：反馈原因； 2：处理结果; 5：商品; 6：服务）")
    private Integer type;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 父id
     */
    @ApiModelProperty(value = "父id")
    private Integer pid;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

}
