package com.lzh.controller;

import com.lzh.pojo.Result;
import com.lzh.pojo.User;
import com.lzh.service.RegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("/api/register")
@Slf4j
@RequestMapping
@CrossOrigin
public class RegisterController {
    @Autowired
    private RegisterService registerService;

    @PostMapping
    public Result register(@RequestBody User user) {
        log.info("注册接口被调用");
        registerService.register(user);
        return Result.success();
    }
}
