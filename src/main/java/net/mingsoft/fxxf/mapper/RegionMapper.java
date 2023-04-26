package net.mingsoft.fxxf.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.mingsoft.fxxf.bean.entity.Region;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionMapper extends BaseMapper<Region> {

    List<Region> underRegListInfoByCurName(@Param("regionName") String regionName);
}
