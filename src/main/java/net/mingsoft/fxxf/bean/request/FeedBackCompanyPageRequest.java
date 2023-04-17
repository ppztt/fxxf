package net.mingsoft.fxxf.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ApiModel("经营者相关分页请求实体")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class FeedBackCompanyPageRequest extends BasePageRequest {

    @ApiModelProperty(name = "search", value = "搜索条件", dataType = "string")
    private String search;


    @ApiModelProperty(name = "type", value = "单位类型 1、放心消费承诺单位 2、无理由退货单位", dataType = "int", example = "1")
    private Integer type = 1;

}
