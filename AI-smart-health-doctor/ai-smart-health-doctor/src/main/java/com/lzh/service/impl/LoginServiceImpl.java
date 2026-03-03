package com.lzh.service.impl;

import com.lzh.mapper.LoginMapper;
import com.lzh.pojo.LoginInfo;
import com.lzh.pojo.PatientProfile;
import com.lzh.pojo.Result;
import com.lzh.pojo.User;
import com.lzh.service.LoginService;
import com.lzh.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private LoginMapper loginMapper;

    @Override
    public LoginInfo login(String username, String password) {
        User login = loginMapper.login(username, password);
        if(login == null) {
            return null; // 登录失败
        }
        // 登录成功，返回 LoginInfo
        // 生成token
        // 如果查到了这个用户（账号密码对的）
        if(login != null){
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", login.getId());
            claims.put("username", login.getUsername());
            String jwtToken = JwtUtils.generateJwt(claims);
            LoginInfo loginInfo = new LoginInfo();
            loginInfo.setId(login.getId());
            loginInfo.setUsername(login.getUsername());
            loginInfo.setToken(jwtToken);
            return loginInfo;
        }
        return null;
    }

}
