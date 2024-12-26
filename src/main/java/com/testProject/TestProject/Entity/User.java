package com.testProject.TestProject.Entity;

import com.testProject.TestProject.Repository.UserUpdateRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

//    private static UserUpdateRepository userRepository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;
    private String mobileNumber;

    @Temporal(TemporalType.DATE)
    private Date dob;



    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    private String pincode;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;



//    public static String getPasswordByUsername(String username) {
//        Optional<User> userOpt = userRepository.findByUsername(username);
//        if (userOpt.isEmpty()) {
//            throw new RuntimeException("User not found");
//        }
//        return userOpt.get().getPassword();
//    }
}
