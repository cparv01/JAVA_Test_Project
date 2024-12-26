package com.testProject.TestProject.dto;

import com.testProject.TestProject.Entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String mobileNumber;
    private String pincode;
    private String dob;
    private Long countryId;
    private Long stateId;
    private Long cityId;


    private String countryName;
    private String stateName;
    private String cityName;


    private String createdAt;
    private Integer age; // Calculated Age
}

