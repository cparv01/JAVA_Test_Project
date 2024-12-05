package com.testProject.TestProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {
    private String username;
    private String password;
    private String email;
    private String mobileNumber;
    private Date dob;

    private Long cityId;      // City ID instead of name
    private Long stateId;     // State ID instead of name
    private Long countryId;   // Country ID instead of name

    private String pincode;
}


