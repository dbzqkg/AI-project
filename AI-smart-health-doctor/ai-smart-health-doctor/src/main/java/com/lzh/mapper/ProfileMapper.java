package com.lzh.mapper;

import com.lzh.pojo.PatientProfile;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProfileMapper {
    //根据登录的 User 的 id，查出他名下所有的家属档案
    List<PatientProfile> getProfilesByUserId(Integer userId);

    void addProfile(PatientProfile profile);

    void updateProfile(PatientProfile profile);

    @Delete("delete from patient_profile where id = #{id} and user_id = #{userId}")
    void deleteProfile(Integer id, Integer userId);
}
