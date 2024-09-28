package com.globitel.controllers;

import com.globitel.enums.Day;
import com.globitel.Main;
import com.globitel.enums.EnrollmentStatus;
import com.globitel.entities.*;
import com.globitel.entities.Class;
import com.globitel.enums.Role;
import com.globitel.exceptions.ConflictException;
import com.globitel.exceptions.DuplicateEntryException;
import com.globitel.exceptions.ResourceNotFoundException;
import com.globitel.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private DepartmentRepo departmentRepo;
    @Autowired
    private ClassRepo classRepo;
    @Autowired
    private InstructorRepo instructorRepo;
    @Autowired
    private Main mainApp;
    @Autowired
    private EnrollmentRepo enrollmentRepo;
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private ReservationRepo reservationRepo;

    public record StudentRecord(
            Integer student_id,
            String name,
            Integer level,
            String email,
            String phone,
            String address,
            String department,
            Integer current_credit_hours,
            Integer cumulative_credit_hours,
            Integer plan_hours
    ) {
    }


    // Get All Students
    @GetMapping("")
    public List<StudentRecord> getAllStudents() {
        return studentRepo.findAll().stream()
                .map(student -> new StudentRecord(
                        student.getID()
                        , student.getName()
                        , student.getLevel()
                        , student.getEmail()
                        , student.getPhone()
                        , student.getAddress()
                        , student.getDepartment().getName()
                        , student.getCurrentCreditHours()
                        , student.getCumulativeCreditHours()
                        , student.getDepartment().getPlanHours()))
                .collect(Collectors.toList());
    }

    // Get a single Student given ID
    @GetMapping("/{id}")
    public StudentRecord getOneStudent(@PathVariable Integer id) {
        return studentRepo.findById(id).map(student -> new StudentRecord(
                        student.getID()
                        , student.getName()
                        , student.getLevel()
                        , student.getEmail()
                        , student.getPhone()
                        , student.getAddress()
                        , student.getDepartment().getName()
                        , student.getCurrentCreditHours()
                        , student.getCumulativeCreditHours()
                        , student.getDepartment().getPlanHours()))
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
    }

    public record newStudent(
            String name,
            Integer level,
            String email,
            String phone,
            String address,
            Integer department_id
//            String password
    ) {
    }

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Transactional
    public void saveStudent(Department department, Student student) {
        try {
            departmentRepo.save(department);
            studentRepo.save(student);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add student", e);
        }
    }

    // Add a new Student
    @PostMapping("")
    public ResponseEntity<String> addStudent(@RequestBody newStudent request) {
        Department department = departmentRepo.findById(request.department_id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with name: " + request.department_id));

        if (mainApp.emailExists(request.email))
            throw new DuplicateEntryException("Email already in use!");

        if (mainApp.phoneExists(request.phone))
            throw new DuplicateEntryException("Phone number already in use!");

        Student student = new Student();
        student.setName(request.name);

//        String firstName = request.name.split(" ")[0];
//        String username = firstName + "_" + student.getID();
//        String encodedPassword = encoder.encode(request.password);
//
//        student.setUsername(username);
//        student.setPassword(encodedPassword);
//        student.setRole(Role.ROLE_STUDENT);

        student.setLevel(request.level);
        student.setEmail(request.email);
        student.setPhone(request.phone);
        student.setAddress(request.address);

        student.setGPA(null);
        student.setCGPA(null);
        student.setCurrentCreditHours(0);
        student.setCumulativeCreditHours(0);
        student.setTotalFailedHours(0);
        department.setStudentCount(department.getStudentCount() + 1);


        student.setDepartment(department);
//        departmentRepo.save(department);
//        studentRepo.save(student);

        saveStudent(department, student);
        return ResponseEntity.status(HttpStatus.CREATED).body("Student added successfully");
    }

    public record editedStudent(
            String name,
            String email,
            String phone,
            String address
    ) {
    }

    // Update existing Student given ID
    @PutMapping("/{id}")
    public ResponseEntity<String> editStudent(@RequestBody editedStudent request, @PathVariable Integer id) {
        Student toEdit = studentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        if (mainApp.emailExists(request.email))
            throw new DuplicateEntryException("Email already in use!");

        if (mainApp.phoneExists(request.phone))
            throw new DuplicateEntryException("Phone number already in use!");

        if (request.name != null && !request.name.isEmpty()) toEdit.setName(request.name);
        if (request.email != null && !request.email.isEmpty()) toEdit.setEmail(request.email);
        if (request.phone != null && !request.phone.isEmpty()) toEdit.setPhone(request.phone);
        if (request.address != null && !request.address.isEmpty()) toEdit.setAddress(request.address);
        studentRepo.save(toEdit);

        return ResponseEntity.ok("Student updated successfully");
    }

    @Transactional
    public void deleteStudent(Department department, Student student) {
        try {
            studentRepo.delete(student);
            departmentRepo.save(department);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete student", e);
        }
    }

    // Delete existing Student given ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Integer id) {
        Student toDelete = studentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        Department department = toDelete.getDepartment();
        department.setStudentCount(department.getStudentCount() - 1);
//        studentRepo.delete(toDelete);
//        departmentRepo.save(department);

        deleteStudent(department, toDelete);
        return ResponseEntity.ok("Student deleted successfully");
    }

    public record ClassID(
            Integer class_id
    ) {
    }

    @Transactional
    public void saveEnrollment(Enrollment enrollment, Class clazz, Course course, Student student) {
        try {
            enrollmentRepo.save(enrollment);
            classRepo.save(clazz);
            courseRepo.save(course);
            studentRepo.save(student);
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign student to class", e);
        }
    }

    // Assign a student to a class given student ID
    @PostMapping("/{studentId}/classes")
    public ResponseEntity<String> assignStudentToClass(@PathVariable Integer studentId, @RequestBody ClassID request) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        // Retrieve the class by ID
        Class clazz = classRepo.findById(request.class_id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID:" + request.class_id));


        Reservation classReservation = clazz.getReservation();
        List<Enrollment> conflicts = enrollmentRepo.findConflictingEnrollments(student, clazz, classReservation.getDays(), classReservation.getStartTime(), classReservation.getEndTime());

        if (!conflicts.isEmpty()) {
            throw new ConflictException("The student has a scheduling conflict with another class.");
        }

        // Check if the student is already enrolled in this class
        if (enrollmentRepo.existsByStudentAndClazz(student, clazz)) {
            throw new IllegalStateException("The student is already enrolled in this class");
        }

        // Check if the student is already enrolled in another class of the same course
        if (enrollmentRepo.existsByStudentAndClazzCourse(student, clazz.getCourse())) {
            throw new IllegalStateException("The student is already enrolled in another class of the same course");
        }

        // Check if the class is full
        if (clazz.getRegistered() >= clazz.getReservation().getPlace().getCapacity()) {
            throw new IllegalStateException("This class is full");
        }

        EnrollmentID id = new EnrollmentID();
        id.setClassID(clazz.getID());
        id.setStudentID(student.getID());

        Enrollment enrollment = new Enrollment();
        enrollment.setID(id);
        enrollment.setClazz(clazz);
        enrollment.setStudent(student);
        enrollment.setEnrollmentStatus(EnrollmentStatus.ACTIVE);
        enrollment.setGrade(null);

        clazz.setRegistered(clazz.getRegistered() + 1);
        clazz.getCourse().setNoOfStudents(clazz.getCourse().getNoOfStudents() + 1);
        student.setCurrentCreditHours(student.getCurrentCreditHours() + clazz.getCourse().getCreditHours());

//        classStudentRepo.save(classStudent);
//        classRepo.save(clazz);

        saveEnrollment(enrollment, clazz, clazz.getCourse(), student);
        return ResponseEntity.ok("Student with ID: " + studentId + " joined the class with ID: " + request.class_id + " successfully");
    }

    public record ClassStudentRecord(
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
            EnrollmentStatus enrollmentStatus,
            Double grade
    ) {
    }

    public record nonActiveClasses(
            Integer class_id,
            String course,
            String department,
            EnrollmentStatus enrollmentStatus,
            Double grade
    ) {
    }

    // Get all classes joined by a student given ID
    @GetMapping("/{id}/classes/all")
    public List<nonActiveClasses> getAllClasses(@PathVariable Integer id) {
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        List<Enrollment> enrollments = enrollmentRepo.findByStudent(student);

        return enrollments.stream().map(classStudent -> {
            Class clazz = classStudent.getClazz();
            return new nonActiveClasses(
                    clazz.getID(),
                    clazz.getCourse().getTitle(),
                    clazz.getCourse().getDepartment().getName(),
                    classStudent.getEnrollmentStatus(),
                    classStudent.getGrade());
        }).collect(Collectors.toList());
    }

    // Get Active classes joined by a student given ID
    @GetMapping("/{id}/classes/active")
    public List<ClassStudentRecord> getActiveClasses(@PathVariable Integer id) {
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        List<Enrollment> enrollments = enrollmentRepo.findActiveEnrollmentsByStudentId(id);

        return enrollments.stream().map(classStudent -> {
            Class clazz = classStudent.getClazz();
            return new ClassStudentRecord(
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
                    classStudent.getEnrollmentStatus(),
                    classStudent.getGrade());
        }).collect(Collectors.toList());
    }

    // Get all classes that a student failed, given ID
    @GetMapping("/{id}/classes/failed")
    public List<nonActiveClasses> getFailedClasses(@PathVariable Integer id) {
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        List<Enrollment> enrollments = enrollmentRepo.findFailedEnrollmentsByStudentId(id);

        return enrollments.stream().map(classStudent -> {
            Class clazz = classStudent.getClazz();
            return new nonActiveClasses(
                    clazz.getID(),
                    clazz.getCourse().getTitle(),
                    clazz.getCourse().getDepartment().getName(),
                    classStudent.getEnrollmentStatus(),
                    classStudent.getGrade());
        }).collect(Collectors.toList());
    }

    // Get all classes that a student passed, given ID
    @GetMapping("/{id}/classes/passed")
    public List<nonActiveClasses> getPassedClasses(@PathVariable Integer id) {
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        List<Enrollment> enrollments = enrollmentRepo.findPassedEnrollmentsByStudentId(id);

        return enrollments.stream().map(classStudent -> {
            Class clazz = classStudent.getClazz();
            return new nonActiveClasses(
                    clazz.getID(),
                    clazz.getCourse().getTitle(),
                    clazz.getCourse().getDepartment().getName(),
                    classStudent.getEnrollmentStatus(),
                    classStudent.getGrade());
        }).collect(Collectors.toList());
    }

    public record Results(
            Integer grade
    ) {
    }

//    @Transactional
//    public void saveEnrollmentUpdates(Enrollment enrollment, Student student) {
//        try {
//            enrollmentRepo.save(enrollment);
//            studentRepo.save(student);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to update enrollment data", e);
//        }
//    }

    // update Enrollment data when a student finishes a class
    @PutMapping("/{student_id}/classes/{class_id}")
    public ResponseEntity<String> updateEnrollment(@RequestBody Results request, @PathVariable Integer student_id, @PathVariable Integer class_id) {
        Student student = studentRepo.findById(student_id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + student_id));
        Class clazz = classRepo.findById(class_id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + class_id));

        // Find the enrollment with student_id and class_id
        Enrollment enrollment = enrollmentRepo.findByStudentAndClass(student_id, class_id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for student id: " + student_id + " and class id: " + class_id));
        Double grade;
        if (request.grade >= 95)
            grade = 4.2;
        else if (request.grade >= 85)
            grade = 4.0;
        else if (request.grade >= 80)
            grade = 3.75;
        else if (request.grade >= 77)
            grade = 3.5;
        else if (request.grade >= 73)
            grade = 3.25;
        else if (request.grade >= 70)
            grade = 3.0;
        else if (request.grade >= 67)
            grade = 2.75;
        else if (request.grade >= 63)
            grade = 2.5;
        else if (request.grade >= 60)
            grade = 2.25;
        else if (request.grade >= 57)
            grade = 2.0;
        else if (request.grade >= 53)
            grade = 1.75;
        else if (request.grade >= 50)
            grade = 1.50;
        else grade = 0.5;

        enrollment.setGrade(grade);
        enrollmentRepo.save(enrollment);

//        saveEnrollmentUpdates(enrollment, student);

        return ResponseEntity.ok("Successfully updated student data for student with ID: " + student_id + ", and class ID: " + class_id);
    }

}
