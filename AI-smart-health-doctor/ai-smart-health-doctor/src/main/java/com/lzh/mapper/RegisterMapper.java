package com.lzh.mapper;

import com.lzh.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface RegisterMapper {
    @Insert("Insert into sys_user(username, password) values(#{username}, #{password})")
    void register(User user);
}
