package net.mingsoft.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
                                   boolean isCreateHeader, HttpServletRequest request, HttpServletResponse response) {
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(isCreateHeader);
        defaultExport(list, pojoClass, fileName, request, response, exportParams);
    }

    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName,
                                   HttpServletRequest request, HttpServletResponse response) {
        defaultExport(list, pojoClass, fileName, request, response, new ExportParams());
    }

    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletRequest request, HttpServletResponse response) {
        defaultExport(list, fileName, request, response);
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
            } else {
                fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            }

            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletRequest request, HttpServletResponse response) {
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        if (workbook != null) {
            downLoadExcel(fileName, request, response, workbook);
        }

    }

    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        } catch (NoSuchElementException e) {
            // throw new NormalException("模板不能为空");
        } catch (Exception e) {
            log.error(e.getMessage());
            // throw new NormalException(e.getMessage());
        }
        return list;
    }

    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows,
                                          Class<T> pojoClass) {
        if (file == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        } catch (NoSuchElementException e) {
            // throw new NormalException("excel文件不能为空");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}