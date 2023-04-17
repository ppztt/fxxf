package net.mingsoft.fxxf.bean.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import net.mingsoft.utils.ExcelCell;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @ClassName: FeedbackExtend
 * @Description TODO
 * @Author Ligy
 * @Date 2020/2/10 15:22
 **/
@ApiModel(value = "FeedbackExtend", description = "留言反馈统计实体类")
@Getter
@Setter
public class FeedbackStat implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 反馈类型(放心消费承诺单位； 线下无理由退货承诺店)
     */
    @ApiModelProperty(value = "反馈类型(放心消费承诺单位； 线下无理由退货承诺店)", required = true)
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
    @ApiModelProperty(value = "处理结果")
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
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 归属地市
     */
    @ExcelCell(index = 0)
    @TableField(exist = false)
    @ApiModelProperty(value = "地市")
    private String city;

    @ApiModelProperty(value = "区县")
    private String district;

    @ApiModelProperty(value = "统计期限开始时间")
    private String startTime;

    @ApiModelProperty(value = "统计期限结束时间")
    private String endTime;

    @ApiModelProperty(value = "当前页")
    private Integer current;

    @ApiModelProperty(value = "条数")
    private Integer size;

    @ExcelCell(index = 1)
    @ApiModelProperty(value = "承诺单位数量")
    private Integer companyTotal;

    @ExcelCell(index = 2)
    @ApiModelProperty(value = "被反馈单位数量")
    private Integer complaintCompanyNum;

    @ExcelCell(index = 3)
    @ApiModelProperty(value = "摘牌数量")
    private Integer takeOff;

    @ExcelCell(index = 4)
    @ApiModelProperty(value = "监督投诉的总条数")
    private Integer complaintTotal;

    @ExcelCell(index = 5)
    @ApiModelProperty(value = "待处理")
    private Integer unprocessed;

    @ExcelCell(index = 6)
    @ApiModelProperty(value = "督促告诫")
    private Integer warning;

    @ExcelCell(index = 7)
    @ApiModelProperty(value = "摘牌、公示")
    private Integer disqualification;

    @ExcelCell(index = 8)
    @ApiModelProperty(value = "投诉问题不存在")
    private Integer nonExistentComplaints;

    @ExcelCell(index = 9)
    @ApiModelProperty(value = "其他")
    private Integer other;

    @ApiModelProperty(value = "角色id")
    private Integer roleId;
}
