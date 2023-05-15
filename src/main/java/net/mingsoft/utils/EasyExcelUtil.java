package net.mingsoft.utils;

import com.alibaba.excel.EasyExcelFactory;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.fxxf.bean.vo.ApplicantsExcelVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Collection;

@Slf4j
public class EasyExcelUtil {

    public static void exportExcel(HttpServletResponse response, Class head, String sheetName, String fileName, Collection<?> data) {
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("x-content-type-options", "nosniff");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            EasyExcelFactory.write(response.getOutputStream(), head).sheet(sheetName).doWrite(data);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BusinessException("文件导出失败请重试");
        }
    }
}
