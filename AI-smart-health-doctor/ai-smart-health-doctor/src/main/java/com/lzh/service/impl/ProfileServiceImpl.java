package com.lzh.service.impl;

import com.lzh.mapper.ProfileMapper;
import com.lzh.pojo.PatientProfile;
import com.lzh.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {
    @Autowired
    private ProfileMapper profileMapper;

    @Override
    public List<PatientProfile> getProfiles(Integer userId) {
        return profileMapper.getProfilesByUserId(userId);
    }

    @Override
    public void addProfile(PatientProfile profile) {
        profileMapper.addProfile(profile);
    }
    @Override
    public void updateProfile(PatientProfile profile) {
        profileMapper.updateProfile(profile);
    }
    @Override
    public void deleteProfile(Integer id, Integer userId) {
        profileMapper.deleteProfile(id, userId);
    }

}
