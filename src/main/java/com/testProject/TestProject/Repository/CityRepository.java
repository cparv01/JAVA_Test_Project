package com.testProject.TestProject.Repository;

import com.testProject.TestProject.Entity.City;
import org.springframework.data.repository.CrudRepository;


public interface CityRepository extends CrudRepository<City, Long> {
    City findByName(String name);

//    City findByNameAndCountryId(String name, Long countryId);

}
