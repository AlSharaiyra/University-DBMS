package com.globitel.entities;

import com.globitel.Day;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table
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

    public Reservation(Integer ID, LocalTime startTime, LocalTime endTime, String whatFor, Set<Day> days, Place place, Class clazz) {
        this.ID = ID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.whatFor = whatFor;
        this.days = days;
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

    public Set<Day> getDays() {
        return days;
    }

    public void setDays(Set<Day> days) {
        this.days = days;
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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(ID, that.ID) && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime) && Objects.equals(whatFor, that.whatFor) && Objects.equals(days, that.days) && Objects.equals(place, that.place) && Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, startTime, endTime, whatFor, days, place, clazz);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "ID=" + ID +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", whatFor='" + whatFor + '\'' +
                ", days=" + days +
                ", place=" + place +
                ", clazz=" + clazz +
                '}';
    }
}
