package net.mingsoft.fxxf.vaild;

import cn.afterturn.easypoi.excel.entity.result.ExcelVerifyHandlerResult;
import cn.afterturn.easypoi.handler.inter.IExcelVerifyHandler;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.fxxf.bean.entity.User;
import net.mingsoft.fxxf.bean.vo.ApplicantsUnitExcelImportVo;
import net.mingsoft.fxxf.mapper.UserMapper;
import net.mingsoft.fxxf.service.impl.CommonDataService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author laijunbao
 */
@Component
public class ApplicantsUnitExcelVerifyHandlerImpl implements IExcelVerifyHandler<ApplicantsUnitExcelImportVo> {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ExcelVerifyHandlerResult verifyHandler(ApplicantsUnitExcelImportVo applicantsUnitExcelImportVo) {
        StringBuilder builder = new StringBuilder();

        if (StringUtils.isNotBlank(applicantsUnitExcelImportVo.getCity())) {
            if (!CommonDataService.isAccessCity(applicantsUnitExcelImportVo.getCity())) {
                builder.append("经营场所-所在市列数据不规范");
                return new ExcelVerifyHandlerResult(false, builder.toString());
            }
        }

        if (StringUtils.isNotBlank(applicantsUnitExcelImportVo.getDistrict())) {
            if (!CommonDataService.isAccessArea(applicantsUnitExcelImportVo.getDistrict())) {
                builder.append("经营场所-所在区县列数据不规范");
                return new ExcelVerifyHandlerResult(false, builder.toString());
            }
        }

        if (StringUtils.isNotBlank(applicantsUnitExcelImportVo.getManagement())) {
            if (!CommonDataService.isAccessManagements(applicantsUnitExcelImportVo.getManagement())) {
                builder.append("经营类别列数据不规范");
                return new ExcelVerifyHandlerResult(false, builder.toString());
            }
        }

        if (StringUtils.isNotBlank(applicantsUnitExcelImportVo.getDetails())) {
            if (!CommonDataService.isAccessDetails(applicantsUnitExcelImportVo.getDetails())) {
                builder.append("类别明细列数据不规范");
                return new ExcelVerifyHandlerResult(false, builder.toString());
            }
        }

        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
        if (user != null) {
            User userExtensionInfo = userMapper.selectById(user.getId());
            if (user.getRoleId() != 1) {
                if (StringUtils.isNotBlank(applicantsUnitExcelImportVo.getCity()) &&
                        !applicantsUnitExcelImportVo.getCity().equals(userExtensionInfo.getCity())) {
                    builder.append("导入数据中有其他地市单位，请核实数据后重新上传");
                    return new ExcelVerifyHandlerResult(false, builder.toString());
                }
            }
        }


        // if(applicantsUnitExcelImportVo.getCcDate() == null){
        //     builder.append("消委会意见日期不能为空");
        // }

        if (StringUtils.isNotBlank(builder)) {
            return new ExcelVerifyHandlerResult(false, builder.toString());
        }

        return new ExcelVerifyHandlerResult(true);
    }

}
