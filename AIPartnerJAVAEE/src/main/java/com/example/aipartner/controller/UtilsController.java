package com.example.aipartner.controller;

import com.example.aipartner.pojo.result.Result;
import com.example.aipartner.utils.AliOSSUtils;
import com.example.aipartner.utils.https.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 工具类控制器
 */
@RestController
public class UtilsController {
    @Autowired
    private AliOSSUtils aliOSSUtils;

    private HttpUtils httpUtils;

    /**
     * 上传文件
     * @param file
     * @return
     */
    @PostMapping("/utils/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String url = aliOSSUtils.upload(file);
        return url;
    }

}
