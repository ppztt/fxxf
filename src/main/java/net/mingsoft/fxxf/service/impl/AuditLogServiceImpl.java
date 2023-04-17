package net.mingsoft.fxxf.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.mingsoft.fxxf.mapper.AuditLogMapper;
import net.mingsoft.fxxf.bean.entity.AuditLog;
import net.mingsoft.fxxf.service.AuditLogService;
import org.springframework.stereotype.Service;

@Service
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog> implements AuditLogService {
}
