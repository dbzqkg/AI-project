package com.lzh.controller;

import com.lzh.pojo.PatientProfile;
import com.lzh.pojo.Result;
import com.lzh.service.ProfileService;
import com.lzh.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/profiles")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @GetMapping
    public Result getProfiles(HttpServletRequest request) {
        // 1. 从请求头获取 token
        String token = request.getHeader("token");
        // 2. 解析 token 获取其中的 userId
        Claims claims = JwtUtils.parseJwt(token);
        Integer userId = (Integer) claims.get("userId");

        // 3. 根据自己的 userId 查自己的就诊人列表
        List<PatientProfile> list = profileService.getProfiles(userId);
        return Result.success(list);
    }

    @PostMapping
    public Result addProfile(PatientProfile profile) {

        return Result.success();
    }
}
