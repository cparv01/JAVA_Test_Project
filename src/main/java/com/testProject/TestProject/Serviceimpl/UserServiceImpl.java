package com.testProject.TestProject.Serviceimpl;


import com.testProject.TestProject.Entity.*;
import com.testProject.TestProject.Repository.*;
import com.testProject.TestProject.Service.JwtTokenUtil;
import com.testProject.TestProject.Service.UserService;
import com.testProject.TestProject.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
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

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private  UserUpdateRepository userUpdateRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private final UserDetailsService userDetailsService;


    @Autowired
    public UserServiceImpl( JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }


    // Register user method (from the UserService interface)
    public ResponseEntity<ResponseDTO> registerUser(UserRegistrationDto userDto) {
    try {
            // Check if username, email, or mobile number already exist
            Optional<User> existingUserByUsername = userRepository.findByUsername(userDto.getUsername());
            Optional<User> existingUserByEmail = userRepository.findByEmail(userDto.getEmail());
            Optional<User> existingUserByMobile = userRepository.findByMobileNumber(userDto.getMobileNumber());
            if (existingUserByUsername.isPresent()) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Username already exists", false, null));
            } else if (existingUserByEmail.isPresent()) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Email already exists", false, null));
            } else if (existingUserByMobile.isPresent()) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Mobile number already exists", false, null));
            }

            // Email Regex validation
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            if (!StringUtils.isEmpty(userDto.getEmail()) && !userDto.getEmail().matches(emailRegex)) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Invalid email format.", false, null));
            }

            // Mobile number validation with country code and the plus sign
            String mobileRegex = "^\\+([1-9]{1}[0-9]{1,3})[0-9]{10,14}$";
            if (!StringUtils.isEmpty(userDto.getMobileNumber()) && !userDto.getMobileNumber().matches(mobileRegex)) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Invalid mobile number format. It should start with the country code (e.g., +91 for India).", false, null));
            }

            // Ensure the mobile number starts with the "+" sign (if it doesn't, we add it)
            String mobileNumber = userDto.getMobileNumber();
            if (!mobileNumber.startsWith("+")) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Mobile number must start with a '+' followed by the country code.", false, null));
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

            //-------------------------------------------------------------------
            State state = stateOpt.get();

            // Validate that the state belongs to the country
            if (!state.getCountry().getId().equals(country.getId())) {
                return ResponseEntity.badRequest().body(new ResponseDTO("State does not belong to the selected country", false, null));
            }

            // Fetch City by ID
            Optional<City> cityOpt = cityRepository.findById(userDto.getCityId());
            if (cityOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseDTO("City not found", false, null));
            }
            City city = cityOpt.get();

            // Validate that the city belongs to the state
            if (!city.getState().getId().equals(state.getId())) {
                return ResponseEntity.badRequest().body(new ResponseDTO("City does not belong to the selected state", false, null));
            }
            //-----------------------------------------------------------------


            // Hash the password
            String hashedPassword = passwordEncoder.encode(userDto.getPassword());

            // Create the User object
            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setPassword(hashedPassword);
            user.setEmail(userDto.getEmail());
            user.setMobileNumber(userDto.getMobileNumber());
            user.setDob((userDto.getDob()));
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

    private final ConcurrentHashMap<String, OtpData> otpCache = new ConcurrentHashMap<>();

    // Generate a 6-digit OTP
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // generates a 6-digit OTP
        return String.valueOf(otp);
    }

    // Store OTP in memory for a user (using email or mobile number)
    private void storeOtp(String emailOrMobile, String otp) {
        long expirationTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1); // OTP expires in 20 minutes
        otpCache.put(emailOrMobile, new OtpData(otp, expirationTime));
        System.out.println("Stored OTP for " + emailOrMobile + " with expiration time " + expirationTime);  // Debug log for OTP storage
    }


    // Check if OTP is valid (using email or mobile number)
    private boolean isOtpValid(String emailOrMobile, String otp) {
        OtpData otpData = otpCache.get(emailOrMobile);

        if (otpData == null) {
            // Log if OTP data is missing
            System.out.println("No OTP found for username: " + emailOrMobile);
            return false;
        }

        // Log OTP data for debugging
        System.out.println("OTP stored for username: " + emailOrMobile + " is " + otpData.getOtp());

        if (System.currentTimeMillis() > otpData.getExpirationTime()) {
            otpCache.remove(emailOrMobile); // Remove expired OTP
            System.out.println("OTP expired for username: " + emailOrMobile);
            return false;
        }

        return otpData.getOtp().equals(otp);
    }


    // OTP data class to store OTP and expiration time
    private static class OtpData {
        private final String otp;
        private final long expirationTime;

        public OtpData(String otp, long expirationTime) {
            this.otp = otp;
            this.expirationTime = expirationTime;
        }

        public String getOtp() {
            return otp;
        }

        public long getExpirationTime() {
            return expirationTime;
        }
    }

    private boolean isValidEmail(String email) {
        // You can use a regex or a simple library method to check if the email format is valid.
        return email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    // Helper method to check if the mobile number is valid (example for India)
    private boolean isValidMobileNumber(String mobileNumber) {
        // Mobile number should be 10 digits, starting with a valid prefix (e.g., +91 for India)
        return mobileNumber != null && mobileNumber.matches("^\\+91\\d{10}$");
    }


    @Override
    public ResponseEntity<ResponseDTO> login(LoginDto loginDto) {
        try {
            // Step 1: Validate the input
            if (StringUtils.isEmpty(loginDto.getUsername())) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Username (email or phone) is required.", false, null));
            }


            String username = loginDto.getUsername().trim();
            boolean isEmail = username.contains("@");

            if (isEmail) {
                if (!isValidEmail(username)) {
                    return ResponseEntity.badRequest().body(new ResponseDTO("Invalid email address.", false, null));
                }
            } else {
                if (!isValidMobileNumber(username)) {
                    return ResponseEntity.badRequest().body(new ResponseDTO("Invalid or incomplete mobile number.", false, null));
                }
            }

            // Step 2: Fetch user by email or phone number
            Optional<User> userOpt = isEmail
                    ? userRepository.findByEmail(username) // Ensure case-insensitive search
                    : userRepository.findByMobileNumber(username);

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Invalid username", false, null));
            }


            User user = userOpt.get();

            // Step 3: Generate OTP and store it locally
            String otp = generateOtp();
            storeOtp(isEmail ? user.getEmail() : user.getMobileNumber(), otp);

            System.out.println("Generated OTP for " + (isEmail ? user.getEmail() : user.getMobileNumber()) + ": " + otp);

            // Step 4: Send OTP
            if (isEmail) {
                String emailBody = String.format(
                        "Dear %s,\n\n" +
                                "Your One-Time Password (OTP) for login is: %s\n\n" +
                                "If you did not request this, please ignore this email.\n\n" +
                                "Best regards,\nYour Application Team",
                        user.getUsername(), otp // Replace `user.getName()` with appropriate field for user name
                );

                emailSenderService.sendEmail(user.getEmail(), "Your OTP Code", emailBody);
                System.out.println("OTP sent to email: " + user.getEmail());
            } else {
                String smsMessage = "This is your OTP code: " + otp;
                twilioService.sendSms(user.getMobileNumber(), smsMessage);
                System.out.println("OTP sent to mobile: " + user.getMobileNumber());
            }

            // Step 5: Generate JWT tokens
            String accessToken = jwtTokenUtil.generateToken(user.getUsername());
            String refreshToken = jwtTokenUtil.generateRefreshToken(user.getUsername());

            // Step 6: Prepare the response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("otp", otp); // for testing purposes, you may remove this
            responseData.put("access_token", accessToken);
            responseData.put("refresh_token", refreshToken);

            return ResponseEntity.ok(new ResponseDTO("Login successful. OTP and tokens generated.", true, responseData));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ResponseDTO("Error during login: " + e.getMessage(), false, null));
        }
    }


    @Override
    public ResponseEntity<ResponseDTO> addCity(CityDto cityDto) {

        Country country = countryRepository.findById(cityDto.getCountryId()).orElse(null);

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

    @Override
    public ResponseEntity<ResponseDTO> addCountry(CountryDTO countryDTO) {

        Country existingCountry = countryRepository.findByName(countryDTO.getName());
        if (existingCountry != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO("Country already exists in the given country", false, null));
        }

        Country country1 = new Country();
        country1.setName(countryDTO.getName());
        Country savedCountry = countryRepository.save(country1);

        return ResponseEntity.ok(new ResponseDTO("Country added successfully", true, savedCountry));
    }

    @Override
    public ResponseEntity<ResponseDTO> verifyOtpAndAuthenticate(VerifyOtpDto verifyOtpDto, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            System.out.println("Step 1: Validate inputs");

            // Step 1: Validate inputs
            if (StringUtils.isEmpty(verifyOtpDto.getOtp()) || StringUtils.isEmpty(verifyOtpDto.getEmailOrMobile())) {
                return ResponseEntity.badRequest().body(new ResponseDTO("OTP and email/mobile number are required.", false, null));
            }

            // Step 2: Extract the token from the Authorization header
            String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Authorization token is missing or invalid.", false, null));
            }

            System.out.println("Email or phone number extract");

            // Extract username (email or phone) from the token
            String username = jwtTokenUtil.extractUsername(token);
            if (username == null) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Invalid token", false, null));
            }

            System.out.println("Extracted username from token: " + username);

            // Step 3: Validate OTP from memory cache using email or mobile number
            if (!isOtpValid(verifyOtpDto.getEmailOrMobile(), verifyOtpDto.getOtp())) {
                System.out.println("For verify Email & mobile " + verifyOtpDto.getEmailOrMobile() + " OTP is ....." + verifyOtpDto.getOtp());
                return ResponseEntity.badRequest().body(new ResponseDTO("Invalid or expired OTP.", false, null));
            }

            // Step 4: Validate the token with the extracted username
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtTokenUtil.validateToken(token, userDetails)) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Invalid token.", false, null));
            }

            System.out.println("Step 5: Authenticate the user");

            // Step 5: Authenticate the user
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            System.out.println("Step 6: OTP validated successfully");

            // Step 6: OTP validated successfully, so remove OTP from cache
            otpCache.remove(verifyOtpDto.getEmailOrMobile());

            return ResponseEntity.ok(new ResponseDTO("OTP and token verified successfully.", true, null));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ResponseDTO("Error during verification", false, null));
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> updateUserDetails(Long userId, updateUser updateUserDto) {
        // Fetch the user by ID
        Optional<User> existingUserOpt = userUpdateRepository.findById(userId);
        if (existingUserOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseDTO("User not found", false, null));
        }

        User existingUser = existingUserOpt.get();
        try {
            System.out.println("Updating user with ID: " + userId);


            // Update fields only if they are provided in `updateUserDto`
            if (updateUserDto.getUsername() != null && !updateUserDto.getUsername().isEmpty()) {
                existingUser.setUsername(updateUserDto.getUsername());
            }
            if (updateUserDto.getEmail() != null && !updateUserDto.getEmail().isEmpty()) {
                existingUser.setEmail(updateUserDto.getEmail());
            }
            if (updateUserDto.getMobileNumber() != null && !updateUserDto.getMobileNumber().isEmpty()) {
                existingUser.setMobileNumber(updateUserDto.getMobileNumber());
            }
            if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isEmpty()) {
                String hashedPassword = passwordEncoder.encode(updateUserDto.getPassword());
                existingUser.setPassword(hashedPassword);
            }
            if (updateUserDto.getDob() != null) {
                existingUser.setDob(updateUserDto.getDob());
            }
            if (updateUserDto.getCountryId() != null) {
                Optional<Country> countryOpt = countryRepository.findById(updateUserDto.getCountryId());
                if (countryOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(new ResponseDTO("Country not found", false, null));
                }
                existingUser.setCountry(countryOpt.get());
            }
            if (updateUserDto.getStateId() != null) {
                Optional<State> stateOpt = stateRepository.findById(updateUserDto.getStateId());
                if (stateOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(new ResponseDTO("State not found", false, null));
                }
                existingUser.setState(stateOpt.get());
            }
            if (updateUserDto.getCityId() != null) {
                Optional<City> cityOpt = cityRepository.findById(updateUserDto.getCityId());
                if (cityOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(new ResponseDTO("City not found", false, null));
                }
                existingUser.setCity(cityOpt.get());
            }
            if (updateUserDto.getPincode() != null && !updateUserDto.getPincode().isEmpty()) {
                existingUser.setPincode(updateUserDto.getPincode());
            }

            // Save the updated user
            User updatedUser = userUpdateRepository.save(existingUser);

            return ResponseEntity.ok(new ResponseDTO("User details updated successfully", true, updatedUser));

        } catch (Exception e) {
//                return ResponseEntity.badRequest().body(new ResponseDTO("Already exists, cannot update", false, null));
        	if (e.getMessage() != null) {
        		
        		   return ResponseEntity.badRequest().body(new ResponseDTO("Email Or Mobile number Or Username Already exists", false, null));
//                 
//        	}
//                if (e.getMessage().contains("Email already exists")) {
//                    return ResponseEntity.badRequest().body(new ResponseDTO("Email already exists, cannot update", false, null));
//                }
//                // Check for mobile number duplication
//                if (e.getMessage().contains("Mobile number already exists")) {
//                    return ResponseEntity.badRequest().body(new ResponseDTO("Mobile number already exists, cannot update", false, null));
//                }
//                // Check for username duplication
//                if (e.getMessage().contains("Username already exists")) {
//                    return ResponseEntity.badRequest().body(new ResponseDTO("Username already exists, cannot update", false, null));
//                }
            }else {
                // Handle other general exceptions (including SQLExceptions) here
                e.printStackTrace();
                return ResponseEntity.status(500).body(new ResponseDTO("Error updating user: " + e.getMessage(), false, null));
            }
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> deleteUser(Long userId) {
        try {
            // Fetch the user by ID
        	
        	System.out.print("Hello");
        	
        	
            Optional<User> existingUserOpt = userRepository.findById(userId);
            if (existingUserOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseDTO("User not found", false, null));
            }

            // Delete the user
            userRepository.deleteById(userId);

            return ResponseEntity.ok(new ResponseDTO("User deleted successfully", true, null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ResponseDTO("Error deleting user: " + e.getMessage(), false, null));
        }
    }


    @Override
    public ResponseEntity<ResponseDTO> getAllUsers() {
        try {
            List<Object[]> usersData = userRepository.getAllUsersWithAge();
            if (usersData.isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseDTO("No users found", false, null));
            }

            // List to hold the mapped UserDTO objects
            List<UserDTO> userDTOList = new ArrayList<>();

            // SimpleDateFormat to format Date to String
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Map the result to UserDTO objects
            for (Object[] row : usersData) {
                UserDTO userDTO = new UserDTO();
                userDTO.setId((Long) row[0]);
                userDTO.setUsername((String) row[1]);
                userDTO.setEmail((String) row[2]);
                userDTO.setMobileNumber((String) row[3]);
                userDTO.setPincode((String) row[4]);

                // Convert dob (java.sql.Date) to String
                Date dob = (Date) row[5];  // dob is a java.sql.Date
                String dobString = (dob != null) ? dateFormat.format(dob) : null;
                userDTO.setDob(dobString);

                // Set city, state, and country details
                userDTO.setCityId((Long) row[6]);
                userDTO.setCityName((String) row[7]);
                userDTO.setStateId((Long) row[8]);
                userDTO.setStateName((String) row[9]);
                userDTO.setCountryId((Long) row[10]);
                userDTO.setCountryName((String) row[11]);

                // Set createdAt
                userDTO.setCreatedAt(row[12].toString());  // Format this if necessary

                // Set age (which is already being calculated in the query)
                userDTO.setAge((Integer) row[13]);

                userDTOList.add(userDTO);
            }

            return ResponseEntity.ok(new ResponseDTO("All users fetched successfully", true, userDTOList));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ResponseDTO("Error fetching users: " + e.getMessage(), false, null));
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> getPlaceUsers() {
        try {
                // Fetch all states, cities, and countries from repositories
                List<State> states = (List<State>) stateRepository.findAll();
                List<City> cities = (List<City>) cityRepository.findAll();
                List<Country> countries = countryRepository.findAll();

                // Map entities to DTOs
                List<StateDto> stateDTOs = states.stream()
                        .map(state -> new StateDto(state.getId(), state.getName(), state.getCountry().getId()))
                        .collect(Collectors.toList());

                List<CityDto> cityDTOs = cities.stream()
                        .map(city -> new CityDto(city.getId(), city.getName(),
                                city.getState().getId(), city.getCountry().getId()))
                        .collect(Collectors.toList());

                List<CountryDTO> countryDTOs = countries.stream()
                        .map(country -> new CountryDTO(country.getId(), country.getName()))
                        .collect(Collectors.toList());

                // Return the data as part of the response
            return ResponseEntity.ok((new ResponseDTO("Place data fetched successfully", true,
                        new Object[] { stateDTOs, cityDTOs, countryDTOs })));
            } catch (Exception e) {
                e.printStackTrace();
            return ResponseEntity.ok( new ResponseDTO("Error fetching places: " + e.getMessage(), false, null));
            }
    }

    @Override
    public ResponseEntity<ResponseDTO> getUser(Long userId){
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        return ResponseEntity.ok(new ResponseDTO("Currnt user details" + userOpt.get(), true, userOpt));
    }
}
