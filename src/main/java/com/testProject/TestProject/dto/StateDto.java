package com.testProject.TestProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StateDto {
    private String name;      // Name of the state
    private Long countryId;
}
