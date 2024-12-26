package com.testProject.TestProject.Repository;

import com.testProject.TestProject.Entity.User;
import com.testProject.TestProject.dto.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByMobileNumber(String mobileNumber);


    @Query("SELECT u.id, u.username, u.email, u.mobileNumber, u.pincode, u.dob, " +
            "u.city.id, u.city.name, u.state.id, u.state.name, u.country.id, u.country.name, u.createdAt, " +
            "fn_CalculateAge(u.dob) AS age " + "FROM User u")
    List<Object[]> getAllUsersWithAge();
}
