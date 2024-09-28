package com.globitel.controllers;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.globitel.enums.ClassStatus;
import com.globitel.enums.Day;
import com.globitel.enums.EnrollmentStatus;
import com.globitel.entities.*;
import com.globitel.entities.Class;
import com.globitel.exceptions.ConflictException;
import com.globitel.exceptions.ResourceNotFoundException;
import com.globitel.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.globitel.controllers.InstructorController.InstructorRecord;

@RestController
@RequestMapping("/api/v1/classes")
public class ClassController {
    @Autowired
    private ClassRepo classRepo;
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private EnrollmentRepo enrollmentRepo;
    @Autowired
    private PlaceRepo placeRepo;
    @Autowired
    private ReservationRepo reservationRepo;
    @Autowired
    private StudentRepo studentRepo;

    public record ClassRecord(
            Integer class_id,
            String place,
            String type,
            Set<Day> days,
            LocalTime startTime,
            LocalTime endTime,
            Integer capacity,
            Integer registered,
            String course,
            String department,
            ClassStatus status
    ) {
    }

    // Get All Classes
    @GetMapping("")
    public List<ClassRecord> getAllClasses() {
        return classRepo.findAll().stream()
                .map(clazz -> new ClassRecord(
                        clazz.getID(),
                        clazz.getReservation().getPlace().getName(),
                        clazz.getReservation().getPlace().getType(),
                        clazz.getReservation().getDays(),
                        clazz.getReservation().getStartTime(),
                        clazz.getReservation().getEndTime(),
                        clazz.getReservation().getPlace().getCapacity(),
                        clazz.getRegistered(),
                        clazz.getCourse().getTitle(),
                        clazz.getCourse().getDepartment().getName(),
                        clazz.getClassStatus()))
                .collect(Collectors.toList());
    }

    // Get a single Class given ID
    @GetMapping("/{id}")
    public ClassRecord getOneClass(@PathVariable Integer id) {
        return classRepo.findById(id).map(clazz -> new ClassRecord(
                        clazz.getID(),
                        clazz.getReservation().getPlace().getName(),
                        clazz.getReservation().getPlace().getType(),
                        clazz.getReservation().getDays(),
                        clazz.getReservation().getStartTime(),
                        clazz.getReservation().getEndTime(),
                        clazz.getReservation().getPlace().getCapacity(),
                        clazz.getRegistered(),
                        clazz.getCourse().getTitle(),
                        clazz.getCourse().getDepartment().getName(),
                        clazz.getClassStatus()))
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID: " + id));
    }

    public record newClass(
            Set<Day> days,
            @JsonFormat(pattern = "HH:mm")
            LocalTime start_time,
            @JsonFormat(pattern = "HH:mm")
            LocalTime end_time,
            Integer place_id,
            Integer course_id
    ) {
    }

    @Transactional
    public void saveClass(Class clazz, Reservation reservation) {
        try {
            classRepo.save(clazz);
            reservationRepo.save(reservation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add class", e);
        }
    }

    // Add a new Class
    @PostMapping("")
    public ResponseEntity<String> addClass(@RequestBody newClass request) {
        Class toAdd = new Class();
        Course course = courseRepo.findById(request.course_id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + request.course_id));

        Place place = placeRepo.findById(request.place_id)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found with ID: " + request.place_id));

        if (request.end_time.isBefore(request.start_time)) {
            throw new IllegalStateException("End time should be after start time");
        }

        List<Reservation> conflicts = reservationRepo.findConflictingReservations(place, request.days, request.start_time, request.end_time);

        if (!conflicts.isEmpty()) {
            throw new ConflictException("There is a reservation conflict at the place: " + place.getName());
        }

        Reservation reservation = new Reservation();

        reservation.setDays(request.days);
        reservation.setStartTime(request.start_time);
        reservation.setEndTime(request.end_time);
        reservation.setWhatFor("Class");
        reservation.setPlace(place);
        toAdd.setCourse(course);
        toAdd.setReservation(reservation);
        toAdd.setClassStatus(ClassStatus.ACTIVE);
        toAdd.setRegistered(0);

//        classRepo.save(toAdd);
//        reservationRepo.save(reservation);

        saveClass(toAdd, reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body("Class added successfully");
    }

//    @Transactional
//    public void deactivateAllActiveClasses() {
//        classRepo.updateAllActiveClassesToPassive();
//    }

    @Transactional
    public void deactivateAllActiveClasses() {
        // Retrieve all active classes
        List<Class> activeClasses = classRepo.findAllActiveClasses();

        // Loop through the list and update each class's status
        for (Class clazz : activeClasses) {
            clazz.setClassStatus(ClassStatus.PASSIVE);
            classRepo.save(clazz);  // Save the updated class
        }
    }

    @PostMapping("/end-semester")
    @Transactional
    public ResponseEntity<String> endSemester() {
        try {
            deactivateAllActiveClasses();
        } catch (Exception e) {
//            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deactivating classes" + e);
        }
        List<Student> students = studentRepo.findAll();

        for (Student student : students) {
            List<Enrollment> activeEnrollments = enrollmentRepo.findActiveEnrollmentsByStudentId(student.getID());
            List<Enrollment> allEnrollments = enrollmentRepo.findAllByStudentId(student.getID());

            double GPA = 0.0, CGPA = 0.0;
            try {
                for (Enrollment enrollment : activeEnrollments) {
                    GPA += enrollment.getGrade() * enrollment.getClazz().getCourse().getCreditHours();
                    if (enrollment.getGrade() >= 1.5) {
                        enrollment.setEnrollmentStatus(EnrollmentStatus.PASSED);
                        student.setCumulativeCreditHours(student.getCumulativeCreditHours() + enrollment.getClazz().getCourse().getCreditHours());
                    } else {
                        enrollment.setEnrollmentStatus(EnrollmentStatus.FAILED);
                        student.setTotalFailedHours(student.getTotalFailedHours() + enrollment.getClazz().getCourse().getCreditHours());
                    }
                    enrollmentRepo.save(enrollment);
                }
                if (student.getCurrentCreditHours() > 0)
                    GPA /= student.getCurrentCreditHours();
                student.setGPA(GPA);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while calculating GPA for student ID: " + student.getID());
            }

            try {
                for (Enrollment enrollment : allEnrollments) {
                    CGPA += enrollment.getGrade() * enrollment.getClazz().getCourse().getCreditHours();
                }
                if (student.getCumulativeCreditHours() == 0)
                    CGPA = GPA;
                else CGPA /= student.getCumulativeCreditHours();
                student.setCGPA(CGPA);

                student.setCurrentCreditHours(0);
                studentRepo.save(student);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while calculating CGPA for student ID: " + student.getID());
            }
        }

        return ResponseEntity.ok("All active classes have been set to passive.");
    }

    public record StudentClassRecord(
            Integer student_id,
            String name,
            Integer level,
            String email,
            String phone,
            String address,
            String department,
            Integer current_credit_hours,
            Integer cumulative_credit_hours,
            Integer plan_hours,
            EnrollmentStatus enrollmentStatus,
            Double grade
    ) {
    }

    // Get all students who joined a class given ID
    @GetMapping("/{id}/students")
    public List<StudentClassRecord> getStudents(@PathVariable Integer id) {
        Class clazz = classRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID: " + id));

        List<Enrollment> enrollments = enrollmentRepo.findByClazz(clazz);

        return enrollments.stream().map(classStudent -> {
            Student student = classStudent.getStudent();
            return new StudentClassRecord(
                    student.getID(),
                    student.getName(),
                    student.getLevel(),
                    student.getEmail(),
                    student.getPhone(),
                    student.getAddress(),
                    student.getDepartment().getName(),
                    student.getCurrentCreditHours(),
                    student.getCumulativeCreditHours(),
                    student.getDepartment().getPlanHours(),
                    classStudent.getEnrollmentStatus(),
                    classStudent.getGrade()
            );
        }).collect((Collectors.toList()));

    }

    // Get the instructor who teaches a class given ID
    @GetMapping("/{id}/instructor")
    public InstructorRecord getInstructor(@PathVariable Integer id) {
        Class clazz = classRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID: " + id));

        Instructor instructor = clazz.getInstructor();
        if (instructor == null) {
            throw new ResourceNotFoundException("Instructor not found for class ID: " + id);
        }

        return new InstructorRecord(
                instructor.getID()
                , instructor.getName()
                , instructor.getEmail()
                , instructor.getPhone()
                , instructor.getAddress()
                , instructor.getDepartment().getName());

    }

}
