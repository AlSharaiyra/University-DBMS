package com.globitel.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table
public class Place {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;
    @Column
    private String type;
    @Column
    private String name;
    @Column
    private Integer capacity;

//    @ManyToOne
//    @JoinColumn(name = "department_id")
//    private Department department;

    @OneToMany(mappedBy = "place")
    private List<Reservation> reservations;

    public Place(Integer ID, String type, String name, Integer capacity, List<Reservation> reservations) {
        this.ID = ID;
        this.type = type;
        this.name = name;
        this.capacity = capacity;
        this.reservations = reservations;
    }

    public Place() {
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return Objects.equals(ID, place.ID) && Objects.equals(type, place.type) && Objects.equals(name, place.name) && Objects.equals(capacity, place.capacity) && Objects.equals(reservations, place.reservations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, type, name, capacity, reservations);
    }

    @Override
    public String toString() {
        return "Place{" +
                "ID=" + ID +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", reservations=" + reservations +
                '}';
    }
}
