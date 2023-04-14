package net.mingsoft.fxxf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * <p>
 * 操作记录
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-09
 */
@TableName("cc_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Setter
@Getter
public class Record extends Model<Record> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    @JsonIgnore
    private Integer id;

    @JsonIgnore
    private Integer feedbackId;

    @JsonIgnore
    private String userid;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作用户")
    private String userName;

    @ApiModelProperty(value = "操作记录")
    private String context;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "操作时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonIgnore
    private LocalDateTime updateTime;

    /**
     * 处理情况
     */
    @ApiModelProperty(value = "处理情况")
    private String processingSituation;

}
