package net.mingsoft.fxxf.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@ApiModel("基础分页请求实体")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class BasePageRequest implements Serializable {

    @ApiModelProperty(name = "current", value = "当前页", dataType = "int", example = "1")
    private Integer current = 1;

    @ApiModelProperty(name = "size", value = "每页条数", dataType = "int", example = "10")
    private Integer size = 10;

}
