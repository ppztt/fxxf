package net.mingsoft.fxxf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 留言反馈
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-09
 */
@TableName("cc_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@ApiModel(value = "Feedback", description = "反馈对象实体")
@Setter
@Getter
public class Feedback extends Model<Feedback> {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "留言反馈 id")
    private Integer id;

    /**
     * 申报单位id
     */
    @ApiModelProperty(value = "申报单位id")
    private Integer applicantsId;

    /**
     * 经营者注册名称
     */
    @ApiModelProperty(value = "经营者注册名称")
    private String regName;

    /**
     * 反馈类型(放心消费单位； 线下无理由退货承诺店)
     */
    @ApiModelProperty(value = "反馈类型(1:放心消费单位； 2:线下无理由退货承诺店)")
    private String type;

    /**
     * 反馈原因
     */
    @ApiModelProperty(value = "反馈原因")
    private String reason;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    private String name;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    private String phone;

    /**
     * 内容
     */
    @ApiModelProperty(value = "内容")
    private String content;

    /**
     * 处理状态(0:待处理；1:已处理)
     */
    @ApiModelProperty(value = "处理状态(0:待处理；1:已处理)")
    private String status;

    /**
     * 处理结果
     */
    @ApiModelProperty(value = "处理结果",required = true)
    private String result;

    /**
     * 处理时间
     */
    @ApiModelProperty(value = "处理时间")
    private LocalDateTime procTime;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间（反馈时间）")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否新消息(0:不是；1:是)
     */
    @ApiModelProperty(value = "是否新消息(0:不是；1:是)")
    private int isNew;

    /**
     * 归属地市
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "归属地市")
    private String city;

    @ApiModelProperty(value = "附件名称")
    private String filesInfo;

    @TableField(exist = false)
    private List<FeedbackUpload> fileList;


}
