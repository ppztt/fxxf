package net.mingsoft.fxxf.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.io.Serializable;

@ApiModel("返回信息处理请求")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class FeedbackHandleRequest implements Serializable {


    @ApiModelProperty(name = "id", value = "经营者ID", dataType = "int", example = "1")
    private Integer id ;

    @NotBlank(message = "处理结果不能为空")
    @ApiModelProperty(name = "result", value = "处理结果")
    private String result;

    @NotBlank(message = "调查处理情况不能为空")
    @ApiModelProperty(name = "processingSituation", value = "调查处理情况")
    private String processingSituation;


}
