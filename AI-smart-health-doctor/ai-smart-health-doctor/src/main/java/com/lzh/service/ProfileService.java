package com.lzh.service;

import com.lzh.pojo.PatientProfile;

import java.util.List;

public interface ProfileService {
    List<PatientProfile> getProfiles(Integer userId);
}
