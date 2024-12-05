package com.testProject.TestProject.Repository;

import com.testProject.TestProject.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByMobileNumber(String mobileNumber);

//    Optional<User> findByEmail(String email);
//    Optional<User> findByMobileNumber(String mobileNumber);
}
