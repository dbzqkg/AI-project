package com.lzh.controller;

import com.lzh.pojo.LoginInfo;
import com.lzh.pojo.PatientProfile;
import com.lzh.pojo.Result;
import com.lzh.pojo.User;
import com.lzh.service.LoginService;
import com.lzh.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
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


    @GetMapping("/profiles")
    public Result getProfiles(HttpServletRequest request) {
        // 1. 从请求头获取 token
        String token = request.getHeader("token");
        // 2. 解析 token 获取其中的 userId
        Claims claims = JwtUtils.parseJwt(token);
        Integer userId = (Integer) claims.get("userId");

        // 3. 根据自己的 userId 查自己的就诊人列表
        List<PatientProfile> list = loginService.getProfiles(userId);
        return Result.success(list);
    }
}
