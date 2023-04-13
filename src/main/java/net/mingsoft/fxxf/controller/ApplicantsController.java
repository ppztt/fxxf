package net.mingsoft.fxxf.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.anno.OperatorLogAnno;
import net.mingsoft.fxxf.entity.Applicants;
import net.mingsoft.fxxf.service.ApplicantsService;
import net.mingsoft.fxxf.vo.ApiResult;
import net.mingsoft.fxxf.vo.ApplicantsPageSearchVo;
import net.mingsoft.fxxf.vo.PageResultLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 申报单位相关接口
 *
 * @author: huangjunjian
 * @date: 2023-04-12
 */
@Api(tags = {"申报单位相关接口"})
@RestController
@RequestMapping("/applicants")
@Slf4j
public class ApplicantsController {

    @Autowired
    private ApplicantsService applicantsService;

    /**
     * 经营者条件分页列表查询
     */
    @GetMapping("/listPage")
    @ApiOperation(value = "经营者条件分页列表查询")
    @OperatorLogAnno(operType = "查询", operModul = "无理由退货承诺", operDesc = "查询经营者列表")
    public ApiResult<PageResultLocal<Applicants>> list(@RequestBody ApplicantsPageSearchVo applicantsPageSearchVo) {
        try {
            IPage<Applicants> page = applicantsService.listPage(applicantsPageSearchVo);

            return ApiResult.success(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.fail(e.getMessage());
        }
    }
}
