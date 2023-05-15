package net.mingsoft.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @param
 * @author laijunbao
 * @description Excel导入导出工具类
 * @updateTime 2019-12-05-0005 16:13
 * @return
 * @throws
 */
@Slf4j
public class ExcelUtil {

    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName,
                                   HttpServletRequest request, HttpServletResponse response) {
        defaultExport(list, pojoClass, fileName, request, response, new ExportParams());
    }


    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletRequest request, HttpServletResponse response,
                                      ExportParams exportParams) {
        Workbook workbook = null;
        List dataList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            dataList.add(list.get(i));
            if (dataList.size() == 10000) {
                workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, dataList);
                dataList.clear();
            }
        }
        workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, dataList);
        if (workbook != null) {
            downLoadExcel(fileName, request, response, workbook);
            dataList.clear();
        }

    }

    public static void downLoadExcel(String fileName, HttpServletRequest request, HttpServletResponse response, Workbook workbook) {
        try {
            if (request.getHeader("USER-AGENT").toLowerCase().contains("firefox")) {
                fileName = "=?UTF-8?B?" + (new String(Base64.getEncoder().encode(fileName.getBytes(StandardCharsets.UTF_8)))) + "?=";
            }
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("downLoadExcel下载文件异常", e);
        }
    }


}