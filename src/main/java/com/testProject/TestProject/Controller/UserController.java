package com.testProject.TestProject.Controller;

import com.testProject.TestProject.Service.UserService;
import com.testProject.TestProject.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto userDto) {
        try {
            return userService.registerUser(userDto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }

    // User Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        try {
            // Call the service method to process the login
            String response = String.valueOf(userService.login(loginDto));

            return ResponseEntity.ok(response); // Return success message if login is successful
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/addCity")
    public ResponseEntity<ResponseDTO> addCity(@RequestBody CityDto cityDto) {
        try{
            return userService.addCity(cityDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/addState")
    public ResponseEntity<ResponseDTO> addState(@RequestBody StateDto stateDto) {
        return userService.addState(stateDto);
    }


//    // Profile Update Example (secured)
//    @PutMapping("/profile")
//    public String updateProfile(@RequestHeader("Authorization") String token) {
//        if (jwtTokenUtil.validateToken(token)) {
//            // Handle profile update logic
//            return "Profile updated";
//        }
//        return "Unauthorized";
//    }

}
