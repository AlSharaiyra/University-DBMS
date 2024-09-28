package com.globitel.repos;

import com.globitel.enums.Day;
import com.globitel.entities.Class;
import com.globitel.entities.Course;
import com.globitel.entities.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Repository
public interface ClassRepo extends JpaRepository<Class, Integer> {
    List<Class> findByCourse(Course course);

    @Query("SELECT c FROM Class c WHERE c.instructor = :instructor AND c != :newClass AND c.classStatus = com.globitel.enums.ClassStatus.ACTIVE AND EXISTS " +
            "(SELECT d FROM Class cl JOIN cl.reservation r JOIN r.days d WHERE cl = c " +
            "AND d IN :days AND r.startTime < :endTime AND r.endTime > :startTime)")
    List<Class> findConflictingClasses(
            @Param("instructor") Instructor instructor,
            @Param("newClass") Class newClass,
            @Param("days") Set<Day> days,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    //    @Modifying
//    @Query("UPDATE Class c SET c.classStatus = com.globitel.enums.ClassStatus.PASSIVE WHERE c.classStatus = com.globitel.enums.ClassStatus.ACTIVE")
//    void updateAllActiveClassesToPassive();
    @Query("SELECT c FROM Class c WHERE c.classStatus = com.globitel.enums.ClassStatus.ACTIVE")
    List<Class> findAllActiveClasses();
}

// ok I wanna do it another way, let`s retrieve all active classes then do a for loop on them and change them from active to passive
