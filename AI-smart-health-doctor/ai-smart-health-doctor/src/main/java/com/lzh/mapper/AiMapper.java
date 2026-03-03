package com.lzh.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiMapper {
    public void updateProfileHistory(Integer profileId, String summary);
}
