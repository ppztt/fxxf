package net.mingsoft.fxxf.bean.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 监督投诉-企业详情分页请求实体
 *
 * @author: huangjunjian
 * @date: 2023-04-14
 */
@ApiModel("经营者相关分页请求实体")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CompanyDetailsPageRequest extends BasePageRequest {
    private String applicantsId;
    private String startTime;
    private String endTime;
}
