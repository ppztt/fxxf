package net.mingsoft.fxxf.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import net.mingsoft.fxxf.entity.Applicants;
import net.mingsoft.fxxf.vo.ApplicantsPageSearchVo;
import net.mingsoft.fxxf.vo.RegionVo;

import java.util.List;

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

    IPage<Applicants> listPage(ApplicantsPageSearchVo applicantsPageSearchVo);
}
