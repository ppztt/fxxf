package net.mingsoft.utils;

import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.vo.FieldForSortting;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Excel导出工具
 *
 * @author Ligy
 * @date 2020/02/10
 */
@Slf4j
public class ExcelExportUtil {
    /**
     * 用来验证excel与Vo中的类型是否一致 <br>
     * Map<栏位类型,只能是哪些Cell类型>
     */
    private static Map<Class<?>, CellType[]> validateMap = new HashMap<>();

    static {
        validateMap.put(String[].class, new CellType[]{CellType.STRING});
        validateMap.put(Double[].class, new CellType[]{CellType.NUMERIC});
        validateMap.put(String.class, new CellType[]{CellType.STRING});
        validateMap.put(Double.class, new CellType[]{CellType.NUMERIC});
        validateMap.put(Date.class, new CellType[]{CellType.NUMERIC, CellType.STRING});
        validateMap.put(Integer.class, new CellType[]{CellType.NUMERIC});
        validateMap.put(Float.class, new CellType[]{CellType.NUMERIC});
        validateMap.put(Long.class, new CellType[]{CellType.NUMERIC});
        validateMap.put(Boolean.class, new CellType[]{CellType.BOOLEAN});
    }

    /**
     * 利用JAVA的反射机制，将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上<br>
     * 用于单个sheet
     *
     * @param <T>
     * @param headers 表格属性列名数组
     * @param dataset 需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     *                javabean属性的数据类型有基本数据类型及String,Date,String[],Double[]
     * @param out     与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @param pattern 如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
     */
    public static <T> void exportExcel(Map<String, String> headers, Collection<T> dataset, OutputStream out,
                                       String pattern, HttpServletResponse response) {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet();

        //表头样式
        CellStyle headTitle = workbook.createCellStyle();
        //居中
        headTitle.setAlignment(HorizontalAlignment.CENTER);
        //25%灰色背景
        headTitle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        //前景
        headTitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headTitle.setBorderLeft(BorderStyle.THIN);
        headTitle.setBorderTop(BorderStyle.THIN);
        headTitle.setBorderRight(BorderStyle.THIN);
        headTitle.setBorderBottom(BorderStyle.THIN);
        //设置垂直对齐的样式为居中对齐;
        headTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        headTitle.setAlignment(HorizontalAlignment.CENTER);
        //自动换行
        headTitle.setWrapText(true);

        //内容样式
        CellStyle contentStyle = workbook.createCellStyle();
        contentStyle.setBorderLeft(BorderStyle.THIN);
        contentStyle.setBorderTop(BorderStyle.THIN);
        contentStyle.setBorderRight(BorderStyle.THIN);
        contentStyle.setBorderBottom(BorderStyle.THIN);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setWrapText(true);

        write2Sheet(sheet, headers, dataset, pattern, headTitle, contentStyle);
        try {
            //workbook.write(out);

            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("监督投诉统计.xls", "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error(e.toString(), e);
        }
    }

    /**
     * 每个sheet的写入
     *
     * @param sheet   页签
     * @param headers 表头
     * @param dataset 数据集合
     * @param pattern 日期格式
     */
    private static <T> void write2Sheet(HSSFSheet sheet, Map<String, String> headers, Collection<T> dataset,
                                        String pattern, CellStyle headTitleStyle, CellStyle contentStyle) {
        //时间格式默认"yyyy-MM-dd"
        if (isBlank(pattern)) {
            pattern = "yyyy-MM-dd";
        }

        //多表头合并；计算格子进行合并；
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 4, 4));

        // 产生表格标题行
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 9));

        //第一行表头
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell6 = row.createCell(5);
        cell6.setCellValue("处理结果");
        cell6.setCellStyle(headTitleStyle);

        //第二行表头
        Set<String> keys = headers.keySet();
        Iterator<String> it1 = keys.iterator();
        //存放临时键变量
        String key = "";
        //标题列数
        int c = 0;
        boolean flag = true;
        while (it1.hasNext()) {
            if (c > 4 && flag) {
                //第二行
                row = sheet.createRow(1);
                flag = false;

                //绘制第二行样式
                HSSFCell cell = row.createCell(0);
                cell.setCellStyle(headTitleStyle);

                cell = row.createCell(1);
                cell.setCellStyle(headTitleStyle);

                cell = row.createCell(2);
                cell.setCellStyle(headTitleStyle);

                cell = row.createCell(3);
                cell.setCellStyle(headTitleStyle);

                cell = row.createCell(4);
                cell.setCellStyle(headTitleStyle);
            }
            key = it1.next();
            if (headers.containsKey(key)) {
                HSSFCell cell = row.createCell(c);
                HSSFRichTextString text = new HSSFRichTextString(headers.get(key));
                cell.setCellValue(text);
                cell.setCellStyle(headTitleStyle);
            }
            c++;
        }

        // 遍历集合数据，产生数据行
        Iterator<T> it = dataset.iterator();
        int index = 1;
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            T t = it.next();
            try {
                if (t instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) t;
                    int cellNum = 0;
                    //遍历列名
                    Iterator<String> it2 = keys.iterator();
                    while (it2.hasNext()) {
                        key = it2.next();
                        if (!headers.containsKey(key)) {
                            log.error("Map 中 不存在 key [" + key + "]");
                            continue;
                        }
                        Object value = map.get(key);
                        HSSFCell cell = row.createCell(cellNum);

                        cellNum = setCellValue(cell, value, pattern, cellNum, null, row, contentStyle);

                        cellNum++;
                    }
                } else {
                    List<FieldForSortting> fields = sortFieldByAnno(t.getClass());
                    int cellNum = 0;
                    for (int i = 0; i < fields.size(); i++) {
                        HSSFCell cell = row.createCell(cellNum);
                        Field field = fields.get(i).getField();
                        field.setAccessible(true);
                        Object value = field.get(t);

                        cellNum = setCellValue(cell, value, pattern, cellNum, field, row, contentStyle);

                        cellNum++;
                    }
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
        }
        // 设定自动宽度
        for (int i = 0; i < headers.size(); i++) {
            //sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, 5000);
        }
    }

    private static int setCellValue(HSSFCell cell, Object value, String pattern, int cellNum, Field field, HSSFRow row, CellStyle contentStyle) {
        String textValue = null;
        if (value instanceof Integer) {
            int intValue = (Integer) value;
            cell.setCellValue(intValue);
        } else if (value instanceof Float) {
            float fValue = (Float) value;
            cell.setCellValue(fValue);
        } else if (value instanceof Double) {
            double dValue = (Double) value;
            cell.setCellValue(dValue);
        } else if (value instanceof Long) {
            long longValue = (Long) value;
            cell.setCellValue(longValue);
        } else if (value instanceof Boolean) {
            boolean bValue = (Boolean) value;
            cell.setCellValue(bValue);
        } else if (value instanceof Date) {
            Date date = (Date) value;
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            textValue = sdf.format(date);
        } else if (value instanceof String[]) {
            String[] strArr = (String[]) value;
            for (int j = 0; j < strArr.length; j++) {
                String str = strArr[j];
                cell.setCellValue(str);
                if (j != strArr.length - 1) {
                    cellNum++;
                    cell = row.createCell(cellNum);
                }
            }
        } else if (value instanceof Double[]) {
            Double[] douArr = (Double[]) value;
            for (int j = 0; j < douArr.length; j++) {
                Double val = douArr[j];
                // 值不为空则set Value
                if (val != null) {
                    cell.setCellValue(val);
                }

                if (j != douArr.length - 1) {
                    cellNum++;
                    cell = row.createCell(cellNum);
                }
            }
        } else {
            // 其它数据类型都当作字符串简单处理
            String empty = "";
            if (field != null) {
                ExcelCell anno = field.getAnnotation(ExcelCell.class);
                if (anno != null) {
                    empty = anno.defaultValue();
                }
            }
            textValue = value == null ? empty : value.toString();
        }
        if (textValue != null) {
            HSSFRichTextString richString = new HSSFRichTextString(textValue);
            cell.setCellValue(richString);
        }
        cell.setCellStyle(contentStyle);
        return cellNum;
    }


    /**
     * 根据annotation的seq排序后的栏位
     *
     * @param clazz
     * @return
     */
    private static List<FieldForSortting> sortFieldByAnno(Class<?> clazz) {
        Field[] fieldsArr = clazz.getDeclaredFields();
        List<FieldForSortting> fields = new ArrayList<>();
        List<FieldForSortting> annoNullFields = new ArrayList<>();
        for (Field field : fieldsArr) {
            ExcelCell ec = field.getAnnotation(ExcelCell.class);
            if (ec == null) {
                // 没有ExcelCell Annotation 视为不汇入
                continue;
            }
            int id = ec.index();
            fields.add(new FieldForSortting(field, id));
        }
        fields.addAll(annoNullFields);
        sortByProperties(fields, true, false, "index");
        return fields;
    }

    private static void sortByProperties(List<? extends Object> list, boolean isNullHigh,
                                         boolean isReversed, String... props) {
        if (CollectionUtils.isNotEmpty(list)) {
            Comparator<?> typeComp = ComparableComparator.getInstance();
            if (isNullHigh == true) {
                typeComp = ComparatorUtils.nullHighComparator(typeComp);
            } else {
                typeComp = ComparatorUtils.nullLowComparator(typeComp);
            }
            if (isReversed) {
                typeComp = ComparatorUtils.reversedComparator(typeComp);
            }

            List<Object> sortCols = new ArrayList<Object>();

            if (props != null) {
                for (String prop : props) {
                    sortCols.add(new BeanComparator(prop, typeComp));
                }
            }
            if (sortCols.size() > 0) {
                Comparator<Object> sortChain = new ComparatorChain(sortCols);
                Collections.sort(list, sortChain);
            }
        }
    }


    private static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        return str.length() == 0;
    }
}
