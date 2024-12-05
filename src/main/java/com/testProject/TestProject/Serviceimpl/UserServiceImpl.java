package com.testProject.TestProject.Serviceimpl;


import com.testProject.TestProject.Entity.City;
import com.testProject.TestProject.Entity.Country;
import com.testProject.TestProject.Entity.State;
import com.testProject.TestProject.Entity.User;
import com.testProject.TestProject.Repository.CityRepository;
import com.testProject.TestProject.Repository.CountryRepository;
import com.testProject.TestProject.Repository.StateRepository;
import com.testProject.TestProject.Repository.UserRepository;
import com.testProject.TestProject.Service.UserService;
import com.testProject.TestProject.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailSenderService emailSenderService;

    // Register user method (from the UserService interface)
    public ResponseEntity<ResponseDTO> registerUser(UserRegistrationDto userDto) {
        try {
            Optional<User> existingUserByUsername = userRepository.findByUsername(userDto.getUsername());
            Optional<User> existingUserByEmail = userRepository.findByEmail(userDto.getEmail());
            Optional<User> existingUserByMobile = userRepository.findByMobileNumber(userDto.getMobileNumber());
            if (existingUserByUsername.isPresent()) {
//                throw new Exception("Username already exists");
                return ResponseEntity.badRequest().body(new ResponseDTO("Username already exists", false, null));
            }else if (existingUserByEmail.isPresent()) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Email already exists", false, null));
            } else if (existingUserByMobile.isPresent()) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Mobile number already exists", false, null));
            }

            // Fetch Country by ID
            Optional<Country> countryOpt = countryRepository.findById(userDto.getCountryId());
            if (countryOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Country not found", false, null));
            }
            Country country = countryOpt.get();

            // Fetch State by ID
            Optional<State> stateOpt = stateRepository.findById(userDto.getStateId());
            if (stateOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseDTO("State not found", false, null));
            }
            State state = stateOpt.get();

            // Fetch City by ID
            Optional<City> cityOpt = cityRepository.findById(userDto.getCityId());
            if (cityOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseDTO("City not found", false, null));
            }
            City city = cityOpt.get();

            // Hash the password
            String hashedPassword = passwordEncoder.encode(userDto.getPassword());

            // Create the User object
            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setPassword(hashedPassword);
            user.setEmail(userDto.getEmail());
            user.setMobileNumber(userDto.getMobileNumber());
            user.setDob(String.valueOf(userDto.getDob()));
            user.setCity(city);
            user.setState(state);
            user.setCountry(country);
            user.setPincode(userDto.getPincode());
            user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            // Save the user
            User savedUser = userRepository.save(user);

            // Return the saved user object along with success message
            return ResponseEntity.ok(new ResponseDTO("User registered successfully", true, savedUser));
        } catch (Exception e) {
            // Log the exception for better debugging
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ResponseDTO("Error registering user: " + e.getMessage(), false, null));
        }
    }


    // JWT generation method (simplified)
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // generates a 6-digit OTP
        return String.valueOf(otp);
    }

    // Login method with OTP generation (from the UserService interface)
    @Override
    public ResponseEntity<ResponseDTO> login(LoginDto loginDto) {
        try {
            // Validate the inputs (username, password, email)
            if (StringUtils.isEmpty(loginDto.getUsername()) || StringUtils.isEmpty(loginDto.getPassword()) || StringUtils.isEmpty(loginDto.getEmail())) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Username, password, and email are required.", false, null));
            }

            // Check if the user exists by username
            Optional<User> user = userRepository.findByUsername(loginDto.getUsername());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Invalid username", false, null));
            }

            // Check if the email matches the one in the database
            if (!user.get().getEmail().equals(loginDto.getEmail())) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Invalid email", false, null));
            }

            if(!user.get().getMobileNumber().equals(loginDto.getPhone())){
                return ResponseEntity.badRequest().body(new ResponseDTO("Invalid mobile", false, null));
            }

            // Match the password using BCrypt
            if (!passwordEncoder.matches(loginDto.getPassword(), user.get().getPassword())) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Invalid password", false, null));
            }

            String otp = generateOtp();
            emailSenderService.sendEmail(user.get().getEmail(), "Your OTP Code", "This is Auto Generated OTP code For testing Purpose is: " + otp);

            System.out.println("OTP sent: " + otp);

            return ResponseEntity.ok(new ResponseDTO("OTP is sent to the registered email.", true, otp));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseDTO("Error during login", false, null));
        }
    }


    @Override
    public ResponseEntity<ResponseDTO> addCity(CityDto cityDto) {
        // Fetch the country by ID
        Country country = countryRepository.findById(cityDto.getCountryId()).orElse(null);

        // If the country does not exist, return an error response
        if (country == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO("Country not found with the given ID", false, null));
        }

        City existingCity = cityRepository.findByName(cityDto.getName());
        if (existingCity != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO("City already exists in the given country", false, null));
        }

        City city = new City();
        city.setName(cityDto.getName());
        city.setCountry(country);

        // Save the city to the database
        City savedCity = cityRepository.save(city);

        return ResponseEntity.ok(new ResponseDTO("City added successfully", true, savedCity));
    }


    @Override
    public ResponseEntity<ResponseDTO> addState(StateDto stateDto) {
                // Fetch the country by ID
        Country country = countryRepository.findById(stateDto.getCountryId()).orElse(null);

                    // If the country does not exist, return an error response
        if (country == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO("Country not found with the given ID", false, null));
        }

        State existingState = stateRepository.findByName(stateDto.getName());
        if (existingState != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO("State already exists in the given country", false, null));
        }

        // Create a new state
        State state = new State();
        state.setName(stateDto.getName());
        state.setCountry(country);

        // Save the state to the database
        State savedState = stateRepository.save(state);

        return ResponseEntity.ok(new ResponseDTO("State added successfully", true, savedState));
    }
}
