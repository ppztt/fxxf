package net.mingsoft.fxxf.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@ApiModel("投诉举报列表分页查询请求")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class FeedbackPageRequest extends BasePageRequest {


    @ApiModelProperty(name = "applicantsId", value = "经营者ID", dataType = "int", example = "1")
    private Integer applicantsId ;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(name = "startTime", value = "开始时间")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(name = "endTime", value = "结束时间")
    private Date endTime;

}
