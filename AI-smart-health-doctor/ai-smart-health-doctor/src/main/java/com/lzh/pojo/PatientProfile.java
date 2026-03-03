package com.lzh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfile {
    private Integer id;
    private Integer userId;
    private String relation;  // 关系：本人、父亲等
    private String realName;
    private String gender;
    private Integer age;
    private Double height;
    private Double weight;
    private String medicalHistory;
    private String aiSummary;
}
