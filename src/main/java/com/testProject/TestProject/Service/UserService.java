package com.testProject.TestProject.Service;

import com.testProject.TestProject.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

public interface UserService{
    ResponseEntity<ResponseDTO> registerUser(UserRegistrationDto userDto);

    ResponseEntity<ResponseDTO> login(LoginDto loginDto);

    ResponseEntity<ResponseDTO> addCity(CityDto cityDto);

    ResponseEntity<ResponseDTO> addState(StateDto stateDto);

    ResponseEntity<ResponseDTO> addCountry(CountryDTO countryDTO);

//    ResponseEntity<ResponseDTO> verifyOtpAndGenerateToken(String username, String otp, String password, String token);

    ResponseEntity<ResponseDTO> verifyOtpAndAuthenticate(VerifyOtpDto verifyOtpDto, @RequestHeader("Authorization") String authorizationHeader);

    ResponseEntity<ResponseDTO> updateUserDetails(Long userId, updateUser userUpdateDto);

    ResponseEntity<ResponseDTO> deleteUser(Long userId);

    ResponseEntity<ResponseDTO> getAllUsers();

    ResponseEntity<ResponseDTO> getPlaceUsers();

    ResponseEntity<ResponseDTO> getUser(Long userId);
}
