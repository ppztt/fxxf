package net.mingsoft.fxxf.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.bean.request.ApplicantsPageRequest;
import net.mingsoft.fxxf.bean.request.ApplicantsStatisticsRequest;
import net.mingsoft.fxxf.bean.request.EnterpriseNewApplyRequest;
import net.mingsoft.fxxf.bean.vo.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 申报单位 服务类
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-09
 */
public interface ApplicantsService extends IService<Applicants> {

    List<RegionVo> getGdRegion();

    IPage<Applicants> listPage(ApplicantsPageRequest applicantsPageRequest);


    ApplicantsFindVo findApplicantsByRegName(Integer id, String creditCode, String type);


    /**
     * 经营者列表-根据id查询单位
     */
    ApplicantsParamsVo findApplicants(Applicants applicants);


    /**
     * 经营者列表-编辑保存
     */
    ApiResult updateApplicants(Integer id, ApplicantsStoreParamsVo2 applicants);

    /**
     * 经营者列表-根据单位id更新状态及原因
     */
    ApiResult updateApplicantsStatus(Integer id, Map<String, String> map);

    /**
     * 经营者列表-模板下载
     */
    void downTemplateFile(Integer type,HttpServletRequest request, HttpServletResponse response);

    ApiResult templatePreImport(MultipartFile file);

    ApiResult audit(Integer id, Integer type, String notes);

    ApiResult<List<Applicants>> templateImport(Map map);

    void export(Integer type,String status, HttpServletRequest request, HttpServletResponse response);

    List<OperatorStatisticsVo> operatorStatistics(ApplicantsStatisticsRequest applicantsStatisticsRequest);

    void operatorStatisticsExport(Integer type,String startTime, String endTime, HttpServletRequest request, HttpServletResponse response);

    ApiResult<EnterpriseUnitNewApplyVo> saveEnterpriseApplyInfo(EnterpriseNewApplyRequest enterpriseNewApplyRequest);
}
