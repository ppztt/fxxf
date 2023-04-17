package net.mingsoft.fxxf.bean.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: FeedbackType
 * @Description 反馈类型/反馈原因 实体类
 * @Author Ligy
 * @Date 2020/1/15 18:14
 **/
@Data
@AllArgsConstructor
public class FeedbackType {

    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("反馈类型(1:放心消费单位； 2:无理由退货实体店)")
    private Integer type;

    @ApiModelProperty("类型名称")
    private String name;

    @ApiModelProperty("父id")
    private Integer pid;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
