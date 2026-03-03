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
    /**
     * 登录接口
      * @param map 包含 username 和 password 的 JSON 对象
      * @return 登录结果，包含用户信息和 JWT token
     */
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> map) {
        LoginInfo loginInfo = loginService.login(map.get("username"), map.get("password"));
        return Result.success(loginInfo);
    }

}
