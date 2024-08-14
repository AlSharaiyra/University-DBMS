package com.globitel.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;
    @Column
    private String days;
    @Column
    private String time;
    @Column
    private String whatFor;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;


    @OneToOne(mappedBy = "reservation")
    private Class clazz;

    public Reservation(Integer ID, String days, String time, String whatFor, Place place, Class clazz) {
        this.ID = ID;
        this.days = days;
        this.time = time;
        this.whatFor = whatFor;
        this.place = place;
        this.clazz = clazz;
    }

    public Reservation() {
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWhatFor() {
        return whatFor;
    }

    public void setWhatFor(String whatFor) {
        this.whatFor = whatFor;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(ID, that.ID) && Objects.equals(days, that.days) && Objects.equals(time, that.time) && Objects.equals(whatFor, that.whatFor) && Objects.equals(place, that.place) && Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, days, time, whatFor, place, clazz);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "ID=" + ID +
                ", days='" + days + '\'' +
                ", time='" + time + '\'' +
                ", whatFor='" + whatFor + '\'' +
                ", place=" + place +
                ", clazz=" + clazz +
                '}';
    }
}
