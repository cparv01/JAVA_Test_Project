package com.testProject.TestProject.Repository;

import com.testProject.TestProject.Entity.Country;
import com.testProject.TestProject.Entity.State;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StateRepository extends CrudRepository<State, Long> {
    State findByName(String name);

//    Optional<State> findByNameAndCountry(String name, Country country);
}
