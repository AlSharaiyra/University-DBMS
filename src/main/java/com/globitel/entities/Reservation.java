package com.globitel.entities;

import com.globitel.enums.Day;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;
    @Column
    private LocalTime startTime;
    @Column
    private LocalTime endTime;
    @Column
    private String whatFor;

    @ElementCollection(targetClass = Day.class)
    @CollectionTable(name = "reservation_days", joinColumns = @JoinColumn(name = "reservation_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day")
    private Set<Day> days;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;


    @OneToOne(mappedBy = "reservation")
    private Class clazz;
}
