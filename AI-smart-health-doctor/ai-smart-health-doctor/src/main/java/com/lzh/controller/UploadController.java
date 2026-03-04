package com.lzh.controller;

import com.lzh.pojo.Result;
import com.lzh.utils.AliyunOSSOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
public class UploadController {
    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;
    @PostMapping("/upload")
    public Result upload (String name, Integer age, MultipartFile file) throws Exception {
        log.info("上传文件:{} {} {}", name, age, file);
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        String url = aliyunOSSOperator.upload(file.getBytes(), originalFilename);
        return Result.success(url);
    }
}
