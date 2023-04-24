package net.mingsoft.fxxf.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.bean.request.ApplicantsPageRequest;
import net.mingsoft.fxxf.bean.vo.OperatorStatisticsVo;
import net.mingsoft.fxxf.bean.vo.RegionVo;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

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


    ArrayList<Applicants> applicantsExport(@Param("type") int type,
                                           @Param("status") String status,
                                           @Param("roleId") Integer roleId,
                                           @Param("cityAccess") String cityAccess,
                                           @Param("districtAccess") String districtAccess);

    ArrayList<RegionVo> getGdRegion();

    ArrayList<Applicants> companyList(String keyword);


    ArrayList<OperatorStatisticsVo> unitOperatorStatistics(
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("roleId") Integer roleId,
            @Param("city") String city,
            @Param("district") String district);


    ArrayList<OperatorStatisticsVo> storeOperatorStatistics(
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("roleId") Integer roleId,
            @Param("city") String city,
            @Param("district") String district);

}
