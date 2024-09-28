package com.globitel.controllers;

import com.globitel.Main;
import com.globitel.entities.*;
import com.globitel.entities.Class;
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
import com.globitel.controllers.ClassController.ClassRecord;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/instructors")
public class InstructorController {
    @Autowired
    private InstructorRepo instructorRepo;
    @Autowired
    private DepartmentRepo departmentRepo;
    @Autowired
    private ClassRepo classRepo;
    @Autowired
    Main mainApp;

    public record InstructorRecord(
            Integer instructor_id,
            String name,
            String email,
            String phone,
            String address,
            String department
    ) {
    }

    // Get All Instructors
    @GetMapping("")
    public List<InstructorRecord> getAllInstructors() {
        return instructorRepo.findAll().stream()
                .map(instructor -> new InstructorRecord(
                        instructor.getID()
                        , instructor.getName()
                        , instructor.getEmail()
                        , instructor.getPhone()
                        , instructor.getAddress()
                        , instructor.getDepartment().getName()))
                .collect(Collectors.toList());
    }

    // Get a single Instructor given ID
    @GetMapping("/{id}")
    public InstructorRecord getOneInstructor(@PathVariable Integer id) {
        return instructorRepo.findById(id).map(instructor -> new InstructorRecord(
                        instructor.getID()
                        , instructor.getName()
                        , instructor.getEmail()
                        , instructor.getPhone()
                        , instructor.getAddress()
                        , instructor.getDepartment().getName()))
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with ID: " + id));
    }

    public record newInstructor(
            String name,
            String email,
            String phone,
            String address,
            Integer department_id
//            String password
    ) {
    }

//    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Transactional
    public void saveInstructor(Department department, Instructor instructor) {
        try {
            departmentRepo.save(department);
            instructorRepo.save(instructor);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add instructor", e);
        }
    }

    // Add a new Instructor
    @PostMapping("")
    public ResponseEntity<String> addInstructor(@RequestBody newInstructor request) {
        Department department = departmentRepo.findById(request.department_id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with name: " + request.department_id));

        if (mainApp.emailExists(request.email))
            throw new DuplicateEntryException("Email already in use!");

        if (mainApp.phoneExists(request.phone))
            throw new DuplicateEntryException("Phone number already in use!");

        Instructor instructor = new Instructor();
        instructor.setName(request.name);

//        String firstName = request.name.split(" ")[0];
//        String username = firstName + "_" + instructor.getID();
//        String encodedPassword = encoder.encode(request.password);
//
//        instructor.setUsername(username);
//        instructor.setPassword(encodedPassword);
//        instructor.setRole(Role.ROLE_INSTRUCTOR);

        instructor.setEmail(request.email);
        instructor.setPhone(request.phone);
        instructor.setAddress(request.address);
        department.setInstructorCount(department.getInstructorCount() + 1);

        instructor.setDepartment(department);
//        departmentRepo.save(department);
//        instructorRepo.save(instructor);

        saveInstructor(department, instructor);
        return ResponseEntity.status(HttpStatus.CREATED).body("Instructor added successfully");
    }

    public record editedInstructor(
            String name,
            String email,
            String phone,
            String address
    ) {
    }

    // Update existing Instructor given ID
    @PutMapping("/{id}")
    public ResponseEntity<String> editInstructor(@RequestBody editedInstructor request, @PathVariable Integer id) {
        Instructor toEdit = instructorRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with ID: " + id));

        if (mainApp.emailExists(request.email))
            throw new DuplicateEntryException("Email already in use!");

        if (mainApp.phoneExists(request.phone))
            throw new DuplicateEntryException("Phone number already in use!");

        if (request.name != null && !request.name.isEmpty()) toEdit.setName(request.name);
        if (request.email != null && !request.email.isEmpty()) toEdit.setEmail(request.email);
        if (request.phone != null && !request.phone.isEmpty()) toEdit.setPhone(request.phone);
        if (request.address != null && !request.address.isEmpty()) toEdit.setAddress(request.address);
        instructorRepo.save(toEdit);

        return ResponseEntity.ok("Instructor updated successfully");
    }

    @Transactional
    public void deleteInstructor(Department department, Instructor instructor) {
        try {
            instructorRepo.delete(instructor);
            departmentRepo.save(department);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete instructor", e);
        }
    }

    // Delete existing Instructor given ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInstructor(@PathVariable Integer id) {
        Instructor toDelete = instructorRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with name: " + id));
        Department department = toDelete.getDepartment();
        department.setStudentCount(department.getStudentCount() - 1);
//        instructorRepo.delete(toDelete);
//        departmentRepo.save(department);

        deleteInstructor(department, toDelete);
        return ResponseEntity.ok("Instructor deleted successfully");
    }

    public record ClassId(
            Integer class_id
    ) {
    }

    @Transactional
    public void saveInstructorAndClass(Instructor instructor, Class clazz) {
        try {
            instructorRepo.save(instructor);
            classRepo.save(clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign class to instructor", e);
        }
    }

    // Assign an instructor to a class given instructor ID
    @PostMapping("/{id}/classes")
    public ResponseEntity<String> assignClassToInstructor(@PathVariable Integer id, @RequestBody ClassId request) {
        Instructor instructor = instructorRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with ID: " + id));

        // Retrieve the class by ID
        Class clazz = classRepo.findById(request.class_id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID:" + request.class_id));

        Reservation classReservation = clazz.getReservation();

        List<Class> conflicts = classRepo.findConflictingClasses(instructor, clazz, classReservation.getDays(), classReservation.getStartTime(), classReservation.getEndTime());

        if (!conflicts.isEmpty()) {
            throw new ConflictException("The instructor has a scheduling conflict with another class.");
        }

        // Check if the class is already assigned to another instructor
        if (clazz.getInstructor() != null && !clazz.getInstructor().equals(instructor)) {
            throw new IllegalStateException("This class is already assigned to another instructor");
        }

        // Check if the instructor is already assigned to this class
        if (instructor.getClasses().contains(clazz)) {
            throw new IllegalStateException("This instructor is already assigned to this class");
        }

        // Assign the class to the instructor
        clazz.setInstructor(instructor);
        instructor.getClasses().add(clazz);

        saveInstructorAndClass(instructor, clazz);
        return ResponseEntity.ok("Instructor with ID: " + id + " assigned the class with ID: " + request.class_id + " successfully");
    }

    // Get all classes taught by an instructor given ID
    @GetMapping("/{id}/classes")
    public List<ClassRecord> getAllClasses(@PathVariable Integer id) {
        Instructor instructor = instructorRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with ID: " + id));

        return instructor.getClasses().stream().map(clazz -> new ClassRecord(
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
}

