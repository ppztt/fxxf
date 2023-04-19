package net.mingsoft.fxxf.vaild;

import cn.afterturn.easypoi.excel.entity.result.ExcelVerifyHandlerResult;
import cn.afterturn.easypoi.handler.inter.IExcelVerifyHandler;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.fxxf.bean.entity.User;
import net.mingsoft.fxxf.bean.vo.ApplicantsStoreExcelImportVo;
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
public class ApplicantsStoreExcelVerifyHandlerImpl implements IExcelVerifyHandler<ApplicantsStoreExcelImportVo> {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ExcelVerifyHandlerResult verifyHandler(ApplicantsStoreExcelImportVo applicantsStoreExcelImportVo) {

        StringBuilder builder = new StringBuilder();

        if (StringUtils.isNotBlank(applicantsStoreExcelImportVo.getCity())) {
            if (!CommonDataService.isAccessCity(applicantsStoreExcelImportVo.getCity())) {
                builder.append("经营场所-所在市列数据不规范");
                return new ExcelVerifyHandlerResult(false, builder.toString());
            }
        }

        if (StringUtils.isNotBlank(applicantsStoreExcelImportVo.getDistrict())) {
            if (!CommonDataService.isAccessArea(applicantsStoreExcelImportVo.getDistrict())) {
                builder.append("经营场所-所在区县列数据不规范");
                return new ExcelVerifyHandlerResult(false, builder.toString());
            }
        }

        if (StringUtils.isNotBlank(applicantsStoreExcelImportVo.getManagement())) {
            if (!CommonDataService.isAccessManagements(applicantsStoreExcelImportVo.getManagement())) {
                builder.append("经营类别列数据不规范");
                return new ExcelVerifyHandlerResult(false, builder.toString());
            }
        }

        if (StringUtils.isNotBlank(applicantsStoreExcelImportVo.getDetails())) {
            if (!CommonDataService.isAccessDetails(applicantsStoreExcelImportVo.getDetails())) {
                builder.append("类别明细列数据不规范");
                return new ExcelVerifyHandlerResult(false, builder.toString());
            }
        }

        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
        if (user != null) {
            User userExtensionInfo = userMapper.selectById(user.getId());
            if (user.getRoleId() != 1) {
                if (StringUtils.isNotBlank(applicantsStoreExcelImportVo.getCity())
                        && !applicantsStoreExcelImportVo.getCity().equals(userExtensionInfo.getCity())) {
                    builder.append("导入数据中有其他地市单位，请核实数据后重新上传");
                    return new ExcelVerifyHandlerResult(false, builder.toString());
                }
            }
        }


        if (StringUtils.isNotBlank(applicantsStoreExcelImportVo.getContents2()) &&
                !StringUtils.isNumeric(applicantsStoreExcelImportVo.getContents2())) {
            builder.append("退货期限（天）只允许输入数字");
        }
        // if(applicantsStoreExcelImportVo.getCcDate() == null){
        //     builder.append("消委会意见日期不能为空");
        // }
        if (StringUtils.isNotBlank(builder)) {
            return new ExcelVerifyHandlerResult(false, builder.toString());
        }
        return new ExcelVerifyHandlerResult(true);
    }

}
