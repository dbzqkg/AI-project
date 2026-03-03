package com.lzh.service;


import com.lzh.pojo.LoginInfo;
import com.lzh.pojo.PatientProfile;
import com.lzh.pojo.User;
import java.util.List;

public interface LoginService {
    LoginInfo login(String username, String password);
}
