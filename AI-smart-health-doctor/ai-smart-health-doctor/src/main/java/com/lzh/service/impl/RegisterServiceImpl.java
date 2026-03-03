package com.lzh.service.impl;

import com.lzh.mapper.RegisterMapper;
import com.lzh.pojo.User;
import com.lzh.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private RegisterMapper registerMapper;

    @Override
    public void register(User user) {
        registerMapper.register(user);
    }
}
