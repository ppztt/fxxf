package net.mingsoft.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLEncoder;

/**
 * @author laijunbao
 */
@Slf4j
public class FileUtil {

    public static ResponseEntity downTemplateFile(String filePath, String fileName){

        File file = null;
        try {
            file = ResourceUtils.getFile(filePath);
        } catch (FileNotFoundException e) {
            log.error("downTemplateFile文件不存在异常",e);
            return new ResponseEntity(fileName + " 文件不存在",HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e){
            log.error("downTemplateFile请求失败异常",e);
            return new ResponseEntity("请求失败",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            if(!file.exists()){
                return new ResponseEntity("文件不存在", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            fileName = URLEncoder.encode(fileName, "utf-8");

            HttpHeaders headers = new HttpHeaders();
            // 通知浏览器以下载文件方式打开
            ContentDisposition contentDisposition =
                    ContentDisposition.builder("attachment").filename(fileName).build();
            headers.setContentDisposition(contentDisposition);
            // application/octet_stream设置MIME为任意二进制数据
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            // 使用apache commons-io 里边的 FileUtils工具类
            //return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(image.getLocation())),
            //        httpHeaders, HttpStatus.OK);
            // 使用spring自带的工具类也可以 FileCopyUtils
            return new ResponseEntity<>(FileCopyUtils.copyToByteArray(file),
                    headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("downTemplateFile异常",e);
        }
        return new ResponseEntity("请求失败",HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
