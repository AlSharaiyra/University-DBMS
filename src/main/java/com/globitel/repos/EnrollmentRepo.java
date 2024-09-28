package com.globitel.repos;

import com.globitel.enums.Day;
import com.globitel.entities.*;
import com.globitel.entities.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EnrollmentRepo extends JpaRepository
        <Enrollment, EnrollmentID> {
    boolean existsByStudentAndClazz(Student student, Class clazz);

    boolean existsByStudentAndClazzCourse(Student student, Course course);

    List<Enrollment> findByStudent(Student student);

    List<Enrollment> findByClazz(Class clazz);

    @Query("SELECT e FROM Enrollment e WHERE e.student = :student AND e.clazz != :newClass AND e.clazz.classStatus = com.globitel.enums.ClassStatus.ACTIVE AND EXISTS " +
            "(SELECT d FROM Class c JOIN c.reservation r JOIN r.days d WHERE c = e.clazz " +
            "AND d IN :days AND r.startTime < :endTime AND r.endTime > :startTime)")
    List<Enrollment> findConflictingEnrollments(
            @Param("student") Student student,
            @Param("newClass") Class newClass,
            @Param("days") Set<Day> days,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

//    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.clazz.enrollmentStatus = com.globitel.enums.EnrollmentStatus.ACTIVE")
//    List<Enrollment> findActiveEnrollmentsByStudentId(@Param("studentId") Integer studentId);
//
//    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.clazz.enrollmentStatus = com.globitel.enums.EnrollmentStatus.FAILED")
//    List<Enrollment> findFailedEnrollmentsByStudentId(@Param("studentId") Integer studentId);
//
//    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.clazz.enrollmentStatus = com.globitel.enums.EnrollmentStatus.PASSED")
//    List<Enrollment> findPassedEnrollmentsByStudentId(@Param("studentId") Integer studentId);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.enrollmentStatus = com.globitel.enums.EnrollmentStatus.ACTIVE")
    List<Enrollment> findActiveEnrollmentsByStudentId(@Param("studentId") Integer studentId);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.enrollmentStatus = com.globitel.enums.EnrollmentStatus.FAILED")
    List<Enrollment> findFailedEnrollmentsByStudentId(@Param("studentId") Integer studentId);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.enrollmentStatus = com.globitel.enums.EnrollmentStatus.PASSED")
    List<Enrollment> findPassedEnrollmentsByStudentId(@Param("studentId") Integer studentId);


    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.clazz.id = :classId")
    Optional<Enrollment> findByStudentAndClass(@Param("studentId") Integer studentId, @Param("classId") Integer classId);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId")
    List<Enrollment> findAllByStudentId(@Param("studentId") Integer studentId);
}
