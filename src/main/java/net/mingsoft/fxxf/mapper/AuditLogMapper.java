package net.mingsoft.fxxf.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.mingsoft.fxxf.bean.entity.AuditLog;
import net.mingsoft.fxxf.bean.vo.AuditLogVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 审核操作记录 Mapper 接口
 */
@Repository
public interface AuditLogMapper extends BaseMapper<AuditLog> {
    List<AuditLogVo> getAuditLog(@Param("id") int id, @Param("userType") Integer userType);
}
