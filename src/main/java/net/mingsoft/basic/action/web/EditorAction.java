/**
 * The MIT License (MIT)
 * Copyright (c) 2012-2022 铭软科技(mingsoft.net)
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */




package net.mingsoft.basic.action.web;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mingsoft.ueditor.MsUeditorActionEnter;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.config.MSProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Locale;
import java.util.Map;

/**
 * 百度编辑器上传
 * @author 铭软开发团队
 * @date 2019年7月16日
 * 历史修订 2022-1-21 新增normalize(),
 *                  editor()方法过滤非法上传路径
 */
@ApiIgnore
@Controller("ueAction")
@RequestMapping("/static/plugins/ueditor/{version}/jsp")
public class EditorAction {

    @ResponseBody
    @RequestMapping(value = "editor", method = {RequestMethod.GET,RequestMethod.POST})
    public String editor(HttpServletRequest request, HttpServletResponse response, String jsonConfig) {
        String uploadMapping = MSProperties.upload.mapping;
        String uploadFloderPath = MSProperties.upload.path;
        if (StringUtils.isNotEmpty(uploadFloderPath) && !uploadFloderPath.startsWith("/")){
            uploadFloderPath = "/"+uploadFloderPath;
        }
        String rootPath = BasicUtil.getRealPath("");
        //如果是绝对路径就直接使用配置的绝对路径
        File saveFloder=new File(uploadFloderPath);
        if (Boolean.TRUE.equals(this.isAbsolute(saveFloder, rootPath))) {
            rootPath = saveFloder.getPath();
            //因为绝对路径已经映射了路径所以隐藏
            jsonConfig = jsonConfig.replace("{ms.upload}", "");
        } else {
            //如果是相对路径替换成配置的路径
            jsonConfig = jsonConfig.replace("{ms.upload}","/"+uploadFloderPath);
        }
        //过滤非法上传路径
        String path = "/"+uploadFloderPath;
        Map<String,Object> map = (Map<String,Object>) JSONObject.parse(jsonConfig);
        String imagePathFormat = (String) map.get("imagePathFormat");
        imagePathFormat = normalize(imagePathFormat,uploadFloderPath);

        String filePathFormat = (String) map.get("filePathFormat");
        filePathFormat = normalize(filePathFormat,uploadFloderPath);

        String videoPathFormat = (String) map.get("videoPathFormat");
        videoPathFormat = normalize(videoPathFormat,uploadFloderPath);

        map.put("imagePathFormat",imagePathFormat);
        map.put("filePathFormat",filePathFormat);
        map.put("videoPathFormat",videoPathFormat);

        jsonConfig = JSONObject.toJSONString(map);

        MsUeditorActionEnter actionEnter = new MsUeditorActionEnter(request, rootPath, jsonConfig, BasicUtil.getRealPath(""));
        String json = actionEnter.exec();
        if (Boolean.TRUE.equals(this.isAbsolute(saveFloder, rootPath))) {
            //如果是配置的绝对路径需要在前缀加上映射路径
            Map data = (Map) JSON.parse(json);
            data.put("url", uploadMapping.replace("/**", "") + data.get("url"));
            return JSON.toJSONString(data);
        }
        return json;
    }

    /**
     * 修复文件的上传路径
     * @param filePath
     * @param uploadFloderPath
     * @return
     */
    private String normalize(String filePath,String uploadFloderPath){
        filePath = FileUtil.normalize(filePath);
        if (!filePath.startsWith(uploadFloderPath)){
            throw new BusinessException("非法路径!");
        }
        return filePath;
    }

    private Boolean isAbsolute(File file, String rootPath) {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return file.isAbsolute();
        } else {
            String path = file.getPath();
            return path.startsWith(rootPath);
        }
    }
}
