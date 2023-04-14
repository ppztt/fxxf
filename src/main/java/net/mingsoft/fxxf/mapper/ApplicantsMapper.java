package net.mingsoft.fxxf.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import net.mingsoft.fxxf.entity.Applicants;
import net.mingsoft.fxxf.request.ApplicantsPageRequest;
import net.mingsoft.fxxf.vo.OperatorStatisticsVo;
import net.mingsoft.fxxf.vo.RegionVo;
import net.mingsoft.fxxf.vo.StoreOperatorStatisticsVo;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 申报单位 Mapper 接口
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-09
 */
@Repository
public interface ApplicantsMapper extends BaseMapper<Applicants> {


    IPage<Applicants> listPage(IPage<T> page, @Param("applicantsPageRequest") ApplicantsPageRequest applicantsPageRequest,
                               @Param("roleId") Integer roleId,
                               @Param("cityAccess") String cityAccess,
                               @Param("districtAccess") String districtAccess);

    IPage<Applicants> applicantsList(
            IPage<T> page,
            @Param("type") int type,
            @Param("city") String city,
            @Param("district") String district,
            @Param("town") String town,
            @Param("status") String status,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("roleId") Integer roleId,
            @Param("cityAccess") String cityAccess,
            @Param("districtAccess") String districtAccess,
            @Param("search") String search,
            @Param("management") String management,
            @Param("details") String details);

    List<Applicants> applicantsExport(@Param("type") int type,
                                      @Param("status") String status,
                                      @Param("roleId") Integer roleId,
                                      @Param("cityAccess") String cityAccess,
                                      @Param("districtAccess") String districtAccess);

    List<RegionVo> getGdRegion();

    List<Applicants> companyList(String keyword);

    IPage<OperatorStatisticsVo> unitOperatorStatistics(
            IPage<T> page,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("roleId") Integer roleId,
            @Param("city") String city,
            @Param("district") String district);

    List<OperatorStatisticsVo> unitOperatorStatistics(
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("roleId") Integer roleId,
            @Param("city") String city,
            @Param("district") String district);

    IPage<StoreOperatorStatisticsVo> storeOperatorStatistics(
            IPage<T> page,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("roleId") Integer roleId,
            @Param("city") String city,
            @Param("district") String district);

    List<StoreOperatorStatisticsVo> storeOperatorStatistics(
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("roleId") Integer roleId,
            @Param("city") String city,
            @Param("district") String district);
}
