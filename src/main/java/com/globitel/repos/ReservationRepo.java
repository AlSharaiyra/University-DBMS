package com.globitel.repos;

import com.globitel.enums.Day;
import com.globitel.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Repository
public interface ReservationRepo extends JpaRepository<Reservation, Integer> {

    @Query("SELECT r FROM Reservation r WHERE r.place = :place AND r.clazz.classStatus = com.globitel.enums.ClassStatus.ACTIVE AND EXISTS " +
            "(SELECT d FROM Reservation res JOIN res.days d WHERE res = r " +
            "AND d IN :days AND r.startTime < :endTime AND r.endTime > :startTime)")
    List<Reservation> findConflictingReservations(
            @Param("place") Place place,
            @Param("days") Set<Day> days,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

}
