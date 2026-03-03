package com.lzh.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfile {
    private Integer id;
    private Integer userId;

    @NotBlank(message = "关系不能为空")
    private String relation;  // 关系：本人、父亲等

    @NotBlank(message = "姓名不能为空")
    private String realName;

    @NotBlank(message = "性别不能为空")
    private String gender;

    @NotNull(message = "年龄不能为空")
    private Integer age;

    private Double height; // 选填，不加注解
    private Double weight; // 选填，不加注解
    private String medicalHistory;
    private String aiSummary;
}
