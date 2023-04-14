package net.mingsoft.fxxf.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.anno.OperatorLogAnno;
import net.mingsoft.fxxf.entity.Applicants;
import net.mingsoft.fxxf.request.ApplicantsPageRequest;
import net.mingsoft.fxxf.service.ApplicantsService;
import net.mingsoft.fxxf.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    @PostMapping("/listPage")
    @ApiOperation(value = "经营者列表-分页列表查询")
//    @OperatorLogAnno(operType = "查询", operModul = "无理由退货承诺", operDesc = "查询经营者列表")
    public ApiResult<PageResultLocal<Applicants>> list(@RequestBody ApplicantsPageRequest applicantsPageRequest) {
        try {
            IPage<Applicants> page = applicantsService.listPage(applicantsPageRequest);

            return ApiResult.success(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.fail(e.getMessage());
        }
    }

    /**
     * 根据 id 查询承诺单位
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "经营者列表-根据id查询单位")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "单位id", required = true)})
//    @OperatorLogAnno(operType = "查询", operModul = "", operDesc = "经营者列表-根据id和经营者注册名称查询是否重复")
    public ApiResult<ApplicantsStoreParamsVo> findApplicantsById(@PathVariable(value = "id") Integer id) {
        try {
            Applicants applicants = applicantsService.getById(id);
            if (applicants != null) {
                return ApiResult.success(applicantsService.findApplicants(applicants));
            } else {
                return ApiResult.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    @GetMapping("/find")
    @ApiOperation(value = "经营者列表-根据id和经营者注册名称查询是否重复", notes = "经营者列表-根据id和经营者注册名称查询是否重复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true),
            @ApiImplicitParam(name = "creditCode", value = "统一社会信用代码", required = true),
    })
//    @OperatorLogAnno(operType = "查询", operModul = "无理由退货承诺", operDesc = "经营者列表-根据 id 查询承诺单位")
    public ApiResult findApplicantsByRegName(@RequestParam(value = "id") Integer id,
                                             @RequestParam(value = "creditCode") String creditCode) {
        try {
            return ApiResult.success(applicantsService.findApplicantsByRegName(id, creditCode));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    /**
     * 根据 id 更新实体店
     */
    @PostMapping("/{id}")
    @ApiOperation(value = "经营者列表-编辑保存", notes = "经营者列表-根据 id 更新单位")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "实体店id", required = true)})
    @DynamicParameters(name = "applicants", properties = {@DynamicParameter(name = "applicants", value = "applicants",
            dataTypeClass = ApplicantsStoreParamsVo.class, required = true)})
//    @OperatorLogAnno(operType = "更新", operModul = "无理由退货承诺", operDesc = "经营者列表-根据 id 更新承诺单位")
    public ApiResult updateApplicants(@PathVariable(value = "id") Integer id, @RequestBody ApplicantsStoreParamsVo2 applicants) {
        try {
            return applicantsService.updateApplicants(id, applicants);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    @PostMapping("/remove/{id}")
    @ApiOperation(value = "经营者列表-根据id删除单位")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "承诺单位id", required = true)})
//    @OperatorLogAnno(operType = "删除", operModul = "无理由退货承诺", operDesc = "经营者列表-根据 id 删除承诺单位")
    public ApiResult delApplicantsById(@PathVariable(value = "id") Integer id) {
        try {
            applicantsService.removeById(id);
            return ApiResult.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }


    @PostMapping("/remove")
    @ApiOperation(value = "经营者列表-根据 ids 删除承诺单位", notes = "经营者列表-根据 ids 删除承诺单位")
    @ApiOperationSupport(params = @DynamicParameters(name = "map", properties = {
            @DynamicParameter(name = "ids", value = "承诺单位ids", required = true, dataTypeClass = List.class)
    }))
//    @OperatorLogAnno(operType = "删除", operModul = "无理由退货承诺", operDesc = "经营者列表-根据 ids 删除承诺单位")
    public ApiResult delApplicantsById(@RequestBody Map map) {
        try {
            List<String> ids = (List<String>) map.get("ids");
            if (!ids.isEmpty()) {
                applicantsService.removeByIds(ids);
                return ApiResult.success();
            }

            return ApiResult.fail("ids为空", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    /**
     * 根据承诺单位 id 摘牌
     */
    @PostMapping("/updateApplicantsStatus/{id}")
    @ApiOperation(value = "经营者列表-根据单位id更新状态及原因", notes = "经营者列表-根据单位id更新状态及原因")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "承诺单位id", required = true)})
    @ApiOperationSupport(params = @DynamicParameters(name = "map", properties = {
            @DynamicParameter(name = "delReason", value = "摘牌具体原因", required = true),
            @DynamicParameter(name = "delOther", value = "摘牌其它必要信息", required = true)}))
//    @OperatorLogAnno(operType = "更新", operModul = "", operDesc = "经营者列表-根据承诺单位 id 摘牌")
    public ApiResult updateApplicantsStatus(@PathVariable(value = "id") Integer id, @RequestBody Map<String, String> map) {
        try {
            return applicantsService.updateApplicantsStatus(id, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    @GetMapping(value = "/downTemplateFile")
    @ApiOperation(value = "经营者列表-模板下载", notes = "模板下载")
//    @OperatorLogAnno(operType = "下载", operModul = "无理由退货承诺", operDesc = "经营者列表-模板下载")
    public void downTemplateFile(HttpServletRequest request, HttpServletResponse response) {
        applicantsService.downTemplateFile(request, response);
    }

    @PostMapping(value = "/preImport")
    @ApiOperation(value = "经营者列表-预导入，检查导入的在期单位名称中是否存在与现有在期名单相同项", notes = "经营者列表-检查导入的在期单位名称中是否存在与现有在期名单相同项")
    @ApiImplicitParam(name = "file", value = "导入文件", required = true, dataType = "__File")
//    @OperatorLogAnno(operType = "导入", operModul = "无理由退货承诺", operDesc = "经营者列表-预导入，检查导入的在期单位名称中是否存在与现有在期名单相同项")
    public ApiResult<List<ExcelImportErrorMsgVo>> templatePreImport(@RequestParam("file") MultipartFile file) {

        if (!Objects.isNull(file) && !file.getOriginalFilename().endsWith(".xlsm")) {
            return ApiResult.fail("上传失败，请上传xlsm格式文件");
        }

        return applicantsService.templatePreImport(file);
    }

    @GetMapping(value = "/audit")
    @ApiOperation(value = "无理由退货承诺单位审核", notes = "无理由退货承诺单位审核。当前登录用户的角色id小于auditRoleId，并且当前状态在4（待审核）、 " +
            "5（县级审核通过）、6（市级审核通过）之一，说明可以审核")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "type", value = "1：审核通过，2：审核不通过", example = "1", dataType = "int", required = true),
                    @ApiImplicitParam(name = "id", value = "承诺单位id", dataType = "int", required = true),
                    @ApiImplicitParam(name = "notes", value = "备注信息", required = true),
            }
    )
//    @OperatorLogAnno(operType = "更新", operModul = "无理由退货承诺", operDesc = "放心消费承诺单位审核")
    public ApiResult audit(@RequestParam(value = "type") Integer type, @RequestParam(value = "id") Integer id,
                           @RequestParam(value = "notes", required = false) String notes) {
        try {
            return applicantsService.audit(id, type, notes);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.fail(e.getMessage());
        }
    }

    //    @RequiresPermissions("wlythcn:upload")
    @PostMapping(value = "/import")
    @ApiOperation(value = "经营者列表-导入", notes = "经营者列表-导入")
    @ApiImplicitParam(name = "map", value = "参数map", required = true)
    @ApiOperationSupport(params = @DynamicParameters(name = "map", properties = {
            @DynamicParameter(name = "fileId", value = "文件id", required = true)
    }))
//    @OperatorLogAnno(operType = "导入", operModul = "", operDesc = "经营者列表-导入")
    public ApiResult<List<Applicants>> templateImport(@RequestBody Map map) {
        return applicantsService.templateImport(map);
    }

    //    @RequiresPermissions("wlythcn:list")
    @GetMapping(value = "/export")
    @ApiOperation(value = "经营者列表-导出", notes = "经营者列表-导出")
    @ApiImplicitParam(name = "status", value = "状态(1:在期； 0:摘牌)，为空则导出全部", example = "1")
    @OperatorLogAnno(operType = "导出", operModul = "无理由退货承诺", operDesc = "经营者列表-导出")
    public void export(@RequestParam(value = "status", required = false) String status, HttpServletRequest request, HttpServletResponse response) {

        applicantsService.export(status, request, response);
    }


    //    @RequiresPermissions("wlythcn:jdtstj")
    @PostMapping("/operatorStatistics/list")
    @ApiOperation(value = "经营者统计-列表", notes = "经营者统计-列表")
    @ApiOperationSupport(params = @DynamicParameters(name = "map", properties = {
            @DynamicParameter(name = "current", value = "当前页", example = "1"),
            @DynamicParameter(name = "size", value = "每页条数", example = "10"),
            @DynamicParameter(name = "startTime", value = "开始时间", required = false),
            @DynamicParameter(name = "endTime", value = "结束时间", required = false),
    }))
//    @OperatorLogAnno(operType = "查询", operModul = "无理由退货承诺", operDesc = "经营者统计-列表")
    public ApiResult<PageResultLocal<StoreOperatorStatisticsVo>> operatorStatistics(@RequestBody Map map) {

        try {
            return ApiResult.success(applicantsService.operatorStatistics(map));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }


    //    @RequiresPermissions("wlythcn:jdtstj")
    @GetMapping("/operatorStatistics/export")
    @ApiOperation(value = "经营者统计-导出", notes = "经营者统计-导出")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "startTime", value = "开始时间", required = false),
                    @ApiImplicitParam(name = "endTime", value = "结束时间", required = false),
            }
    )
//    @OperatorLogAnno(operType = "导出", operModul = "无理由退货承诺", operDesc = "经营者统计-列表导出")
    public void operatorStatisticsExport(@RequestParam(value = "startTime", required = false) String startTime,
                                         @RequestParam(value = "endTime", required = false) String endTime,
                                         HttpServletRequest request, HttpServletResponse response) {

        try {
            applicantsService.operatorStatisticsExport(startTime, endTime, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @PostMapping("/apply/input")
    @ApiOperation(value = "无理由退货承诺新录入", notes = "无理由退货承诺新录入")
    @DynamicParameters(
            name = "enterpriseStoreNewApplyVo",
            properties = {
                    @DynamicParameter(name = "enterpriseStoreNewApplyVo", value = "enterpriseStoreNewApplyVo", dataTypeClass = EnterpriseStoreNewApplyVo.class, required = true)
            }
    )
//    @OperatorLogAnno(operType = "新增", operModul = "无理由退货承诺", operDesc = "放心消费承诺新录入")
    public ApiResult<EnterpriseStoreNewApplyVo> saveEnterpriseApplyInfo(@RequestBody EnterpriseStoreNewApplyVo enterpriseStoreNewApplyVo) {
        try {
            applicantsService.saveEnterpriseApplyInfo(enterpriseStoreNewApplyVo);

            return ApiResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.fail(e.getMessage());
        }
    }
}
