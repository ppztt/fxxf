package net.mingsoft.fxxf.service.impl;


import net.mingsoft.fxxf.entity.AuditLog;
import net.mingsoft.fxxf.service.AuditLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MyAuditLogService {
    @Resource
    private AuditLogService auditLogService;

    public void saveAuditLog(AuditLog auditLog) {
        auditLogService.save(auditLog);
    }

    public void updateAuditLog(AuditLog auditLog) {
        auditLogService.updateById(auditLog);
    }
}
