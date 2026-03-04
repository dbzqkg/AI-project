package com.lzh.controller;

import com.lzh.pojo.PatientProfile;
import com.lzh.pojo.Result;
import com.lzh.service.ProfileService;
import com.lzh.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/api/profiles")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    /**
     * 获取就诊人列表
     *
     * @param request
     * @return
     */
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

    /**
     * 添加就诊人信息
     *
     * @param profile
     * @param token
     * @return
     */
    @PostMapping
    public Result addProfile(
            @Validated @RequestBody PatientProfile profile,// 从请求体获取 profile 数据并校验
            @RequestHeader("token") String token
    ) {
        // 解析 token 获取其中的 userId
        Claims claims = JwtUtils.parseJwt(token);
        Integer userId = (Integer) claims.get("userId");

        profile.setUserId(userId);
        profileService.addProfile(profile);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result updateProfile(
            @PathVariable Integer id,
            @Validated @RequestBody PatientProfile profile,
            @RequestHeader("token") String token
    ) {
        // 解析 token 获取其中的 userId
        Claims claims = JwtUtils.parseJwt(token);
        Integer userId = (Integer) claims.get("userId");
        // 更新就诊人信息
        profile.setId(id);
        profile.setUserId(userId); // Fixed: Set the userId so the Mapper can match the record
        profileService.updateProfile(profile);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteProfile(
            @PathVariable Integer id,
            @RequestHeader("token") String token
    ) {
        // 解析 token 获取其中的 userId
        Claims claims = JwtUtils.parseJwt(token);
        Integer userId = (Integer) claims.get("userId");
        // 删除就诊人信息
        profileService.deleteProfile(id, userId);
        return Result.success();
    }
}