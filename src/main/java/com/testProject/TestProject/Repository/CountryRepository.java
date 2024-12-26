package com.testProject.TestProject.Repository;

import com.testProject.TestProject.Entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Country findByName(String name);
    Optional<Country> findById(Long id);
//    Optional<Country> findByName(String name);
}

