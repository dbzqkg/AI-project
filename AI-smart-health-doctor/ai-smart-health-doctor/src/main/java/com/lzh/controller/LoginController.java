package com.lzh.controller;

import com.lzh.pojo.LoginInfo;
import com.lzh.pojo.PatientProfile;
import com.lzh.pojo.Result;
import com.lzh.pojo.User;
import com.lzh.service.LoginService;
import com.lzh.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/auth")
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> map) {
        LoginInfo loginInfo = loginService.login(map.get("username"), map.get("password"));
        return Result.success(loginInfo);
    }


    @GetMapping("/profiles/{userId}")
    public Result getProfiles(@PathVariable Integer userId) {
        List<PatientProfile> list = loginService.getProfiles(userId);
        return Result.success(list);
    }
}
