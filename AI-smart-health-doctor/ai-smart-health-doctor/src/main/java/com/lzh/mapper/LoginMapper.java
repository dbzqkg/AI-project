package com.lzh.mapper;

import com.lzh.pojo.PatientProfile;
import com.lzh.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoginMapper {
    //登录校验，返回 User 对象
    User login(@Param("username") String username, @Param("password") String password);

    //根据档案主键查询（用于对话前加载背景）
    PatientProfile getProfileById(Integer id);

    //覆盖更新病史（由 AI 总结触发）
    void updateProfileHistory(@Param("id") Integer id, @Param("history") String history);
}