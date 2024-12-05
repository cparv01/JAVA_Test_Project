package com.testProject.TestProject.Service;

import com.testProject.TestProject.dto.*;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<ResponseDTO> registerUser(UserRegistrationDto userDto);

    ResponseEntity<ResponseDTO> login(LoginDto loginDto);

    ResponseEntity<ResponseDTO> addCity(CityDto cityDto);

    ResponseEntity<ResponseDTO> addState(StateDto stateDto);
}
