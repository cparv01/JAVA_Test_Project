package com.testProject.TestProject.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data

@AllArgsConstructor
@NoArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "state_id") // Add this relationship for state
    private State state;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;



//    @ManyToOne
//    @JoinColumn(name = "state_id")  // Foreign key column for State
//    private State state;

}
