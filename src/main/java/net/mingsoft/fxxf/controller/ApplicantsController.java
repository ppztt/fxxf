package net.mingsoft.fxxf.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.bean.request.*;
import net.mingsoft.fxxf.bean.vo.*;
import net.mingsoft.fxxf.service.ApplicantsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
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
    public ApiResult<BasePageResult<Applicants>> list(@RequestBody ApplicantsPageRequest applicantsPageRequest) {
        try {
            return applicantsService.listPage(applicantsPageRequest);
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
    public ApiResult<ApplicantsParamsVo> findApplicantsById(@PathVariable(value = "id") Integer id) {
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
    public ApiResult findApplicantsByRegName(@RequestParam(value = "id") Integer id,
                                             @RequestParam(value = "creditCode") String creditCode,
                                             @RequestParam(value = "type") String type) {
        try {
            return ApiResult.success(applicantsService.findApplicantsByRegName(id, creditCode, type));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    /**
     * 根据 id 更新实体店
     */
    @PostMapping("/update/{id}")
    @ApiOperation(value = "经营者列表-编辑保存", notes = "经营者列表-根据 id 更新单位")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "实体店id", required = true)})
    @DynamicParameters(name = "applicants", properties = {@DynamicParameter(name = "applicants", value = "applicants",
            dataTypeClass = ApplicantsStoreParamsVo.class, required = true)})
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
     * 根据经营者id更新状态及原因
     */
    @PostMapping("/updateApplicantsStatus")
    @ApiOperation(value = "根据经营者id更新状态及原因", notes = "根据经营者id更新状态及原因")
    public ApiResult updateApplicantsStatus(@RequestBody ApplicantsStatusUpdateRequest applicantsStatusUpdateRequest) {
        try {
            return applicantsService.updateApplicantsStatus(applicantsStatusUpdateRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    @GetMapping(value = "/downTemplateFile/{type}")
    @ApiOperation(value = "经营者列表-模板下载", notes = "模板下载")
    public void downTemplateFile(@PathVariable(value = "type") Integer type, HttpServletRequest request, HttpServletResponse response) {
        applicantsService.downTemplateFile(type, request, response);
    }

    @PostMapping(value = "/preImport")
    @ApiOperation(value = "经营者列表-预导入，检查导入的在期单位名称中是否存在与现有在期名单相同项")
    public ApiResult<List<ExcelImportErrorMsgVo>> templatePreImport(@RequestParam("file") MultipartFile file) {

        if (!Objects.isNull(file) && !file.getOriginalFilename().endsWith(".xlsm")) {
            return ApiResult.fail("上传失败，请上传xlsm格式文件");
        }

        return applicantsService.templatePreImport(file);
    }

    @GetMapping(value = "/audit")
    @ApiOperation(value = "经营者审核")
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
    @ApiOperation(value = "经营者列表-导入")
    public ApiResult<List<Applicants>> templateImport(@RequestBody Map map) {
        return applicantsService.templateImport(map);
    }

    //    @RequiresPermissions("wlythcn:list")
    @GetMapping(value = "/export")
    @ApiOperation(value = "经营者列表-导出", notes = "经营者列表-导出")
    @ApiImplicitParam(name = "status", value = "状态(1:在期； 0:摘牌)，为空则导出全部", example = "1")
    public void export(@RequestParam(value = "type") Integer type,
                            @RequestParam(value = "status", required = false) String status,
                            HttpServletRequest request, HttpServletResponse response) {

        applicantsService.export(type, status, request, response);
    }


    //    @RequiresPermissions("wlythcn:jdtstj")
    @PostMapping("/operatorStatistics/list")
    @ApiOperation(value = "经营者统计-列表", notes = "经营者统计-列表")
    public ApiResult<List<OperatorStatisticsVo>> operatorStatistics(@RequestBody ApplicantsStatisticsRequest applicantsStatisticsRequest) {

        try {
            return applicantsService.operatorStatistics(applicantsStatisticsRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    //    @RequiresPermissions("wlythcn:jdtstj")
    @GetMapping("/operatorStatistics/export")
    @ApiOperation(value = "经营者统计-导出", notes = "经营者统计-导出")
    public void operatorStatisticsExport(@RequestParam(value = "type") Integer type,
                                              @RequestParam(value = "startTime", required = false) String startTime,
                                              @RequestParam(value = "endTime", required = false) String endTime,
                                              HttpServletRequest request, HttpServletResponse response) {

        try {
            applicantsService.operatorStatisticsExport(type, startTime, endTime, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @PostMapping("/apply/input")
    @ApiOperation(value = "经营者企业数据记录录入")
    public ApiResult saveEnterpriseApplyInfo(@RequestBody EnterpriseNewApplyRequest enterpriseNewApplyRequest) {
        try {
            return applicantsService.saveEnterpriseApplyInfo(enterpriseNewApplyRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.fail(e.getMessage());
        }
    }
}
