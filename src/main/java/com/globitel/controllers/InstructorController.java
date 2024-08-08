package com.globitel.controllers;

import com.globitel.Main;
import com.globitel.entities.Department;
import com.globitel.entities.Instructor;
import com.globitel.entities._Class;
import com.globitel.exceptions.DuplicateEntryException;
import com.globitel.exceptions.ResourceNotFoundException;
import com.globitel.repos.DepartmentRepo;
import com.globitel.repos.InstructorRepo;
import com.globitel.repos.StudentRepo;
import com.globitel.repos._ClassRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.globitel.controllers.DepartmentController.InstructorRecord;
import com.globitel.controllers.CourseController.ClassRecord;


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
    private _ClassRepo classRepo;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    Main mainApp;

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
    ) {
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
        instructor.setEmail(request.email);
        instructor.setPhone(request.phone);
        instructor.setAddress(request.address);
        department.setInstructorCount(department.getInstructorCount() + 1);

        instructor.setDepartment(department);
        departmentRepo.save(department);
        instructorRepo.save(instructor);

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

    // Delete existing Instructor given ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInstructor(@PathVariable Integer id) {
        Instructor toDelete = instructorRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with name: " + id));
        Department department = toDelete.getDepartment();
        department.setStudentCount(department.getStudentCount() - 1);
        instructorRepo.delete(toDelete);
        departmentRepo.save(department);

        return ResponseEntity.ok("Instructor deleted successfully");
    }

    public record ClassId(
            Integer class_id
    ) {
    }

    // Assign an instructor to a class given instructor ID
    @PostMapping("/{id}/classes")
    public ResponseEntity<String> assignClassToInstructor(@PathVariable Integer id, @RequestBody ClassId request) {
        Instructor instructor = instructorRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with ID: " + id));

        // Retrieve the class by ID
        _Class _class = classRepo.findById(request.class_id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID:" + request.class_id));

        // Add the class to the instructor's list of classes
        if (!instructor.getClasses().contains(_class) && _class.getInstructor() == null) {
            _class.setInstructor(instructor);
            instructor.getClasses().add(_class);
        }
        else if (_class.getInstructor() != null)
            throw new IllegalStateException("This class is already assigned to another instructor");

        else throw new IllegalStateException("This class is already assigned to this instructor");

        // Save the updated entities
        instructorRepo.save(instructor);
        classRepo.save(_class);

        return ResponseEntity.ok("Instructor with ID: " + id + " assigned the class with ID: " + request.class_id + " successfully");
    }

    // Get all classes taught by an instructor given ID
    @GetMapping("/{id}/classes")
    public List<ClassRecord> getAllClasses(@PathVariable Integer id) {
        Instructor instructor = instructorRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with ID: " + id));

        return instructor.getClasses().stream().map(_class -> new CourseController.ClassRecord(
                        _class.getID()
                        , _class.getDays()
                        , _class.getTime()
                        , _class.getHall_lab()
                        , _class.getCapacity()
                        , _class.getRegistered()
                        , _class.getCourse().getTitle()
                        , _class.getCourse().getDepartment().getName()))
                .collect(Collectors.toList());
    }
}

