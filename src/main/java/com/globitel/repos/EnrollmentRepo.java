package com.globitel.repos;

import com.globitel.Day;
import com.globitel.entities.*;
import com.globitel.entities.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Repository
public interface EnrollmentRepo extends JpaRepository
        <Enrollment, EnrollmentID> {
    boolean existsByStudentAndClazz(Student student, Class clazz);

    boolean existsByStudentAndClazzCourse(Student student, Course course);

    List<Enrollment> findByStudent(Student student);

    List<Enrollment> findByClazz(Class clazz);

//    @Query("SELECT e FROM Enrollment e WHERE e.student = :student AND e.clazz != :newClass AND EXISTS (SELECT 1 FROM Reservation r WHERE r = e.clazz.reservation AND :newDay MEMBER OF r.days AND r.startTime < :newEndTime AND r.endTime > :newStartTime)")

    @Query("SELECT e FROM Enrollment e WHERE e.student = :student AND e.clazz != :newClass AND EXISTS " +
            "(SELECT d FROM Class c JOIN c.reservation r JOIN r.days d WHERE c = e.clazz " +
            "AND d IN :days AND r.startTime < :endTime AND r.endTime > :startTime)")
    List<Enrollment> findConflictingEnrollments(
            @Param("student") Student student,
            @Param("newClass") Class newClass,
            @Param("days") Set<Day> days,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

}
