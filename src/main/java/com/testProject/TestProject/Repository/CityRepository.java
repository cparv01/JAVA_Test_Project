package com.testProject.TestProject.Repository;

import com.testProject.TestProject.Entity.City;
import com.testProject.TestProject.Entity.Country;
import com.testProject.TestProject.Entity.State;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CityRepository extends CrudRepository<City, Long> {
    City findByName(String name);

    City findByNameAndCountryId(String name, Long countryId);

//    Optional<City> findByNameAndStateAndCountry(String name, State state, Country country);

}
