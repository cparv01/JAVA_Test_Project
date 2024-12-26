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
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            return userService.login(loginDto);
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

    @PostMapping("/addCountry")
    public ResponseEntity<ResponseDTO> addCountry(@RequestBody CountryDTO countryDto) {
        return userService.addCountry(countryDto);
    }

//    @PostMapping("/verifyOtp")
//    public ResponseEntity<ResponseDTO> verifyOtpAndGenerateToken(
//            @RequestParam String username,
//            @RequestParam String otp,
//            @RequestParam String password,  // New parameter for password
//            @RequestHeader("Authorization") String token) {
//        return userService.verifyOtpAndGenerateToken(username, otp, password, token);
//    }


    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtpAndAuthenticate(@RequestBody VerifyOtpDto verifyOtpDto, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            return userService.verifyOtpAndAuthenticate(verifyOtpDto, authorizationHeader);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Verification failed: " + e.getMessage());
        }
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<ResponseDTO> updateUser(
            @PathVariable("id") Long userId,
            @RequestBody updateUser userUpdateDto) {
        return userService.updateUserDetails(userId, userUpdateDto);
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable("id") Long userId) {
        System.out.println("deleteUser");
        return userService.deleteUser(userId);
    }

    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/getPlace")
    public ResponseEntity<ResponseDTO> getplace() {
        return userService.getPlaceUsers();
    }

    @GetMapping("/getUser/{userId}")
    public ResponseEntity<ResponseDTO> getUser(@PathVariable("userId") Long userId) {
        return userService.getUser(userId);
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
