package com.testProject.TestProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityDto {

    private Long id;
    private String name;      // Name of the city

    private Long stateId;
    private Long countryId;
}
