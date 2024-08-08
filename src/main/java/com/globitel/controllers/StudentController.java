package com.globitel.controllers;

import com.globitel.Main;
import com.globitel.entities.Department;
import com.globitel.entities.Student;
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
import com.globitel.controllers.DepartmentController.StudentRecord;
import com.globitel.controllers.CourseController.ClassRecord;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private DepartmentRepo departmentRepo;
    @Autowired
    private _ClassRepo classRepo;
    @Autowired
    private InstructorRepo instructorRepo;
    @Autowired
    private Main mainApp;

    // Get All Students
    @GetMapping("")
    public List<StudentRecord> getAllStudents() {
        return studentRepo.findAll().stream()
                .map(student -> new DepartmentController.StudentRecord(
                        student.getID()
                        , student.getName()
                        , student.getLevel()
                        , student.getEmail()
                        , student.getPhone()
                        , student.getAddress()
                        , student.getDepartment().getName()
                        , student.getTotalCreditHours()))
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
                        , student.getTotalCreditHours()))
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
    }

    public record newStudent(
            String name,
            Integer level,
            String email,
            String phone,
            String address,
            Integer department_id
    ) {
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
        student.setLevel(request.level);
        student.setEmail(request.email);
        student.setPhone(request.phone);
        student.setAddress(request.address);
        department.setStudentCount(department.getStudentCount() + 1);


        student.setDepartment(department);
        departmentRepo.save(department);
        studentRepo.save(student);

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

    // Delete existing Student given ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Integer id) {
        Student toDelete = studentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        Department department = toDelete.getDepartment();
        department.setStudentCount(department.getStudentCount() - 1);
        studentRepo.delete(toDelete);
        departmentRepo.save(department);

        return ResponseEntity.ok("Student deleted successfully");
    }

    public record ClassId(
            Integer class_id
    ) {
    }

    // Assign a student to a class given student ID
    @PostMapping("/{id}/classes")
    public ResponseEntity<String> assignClassToStudent(@PathVariable Integer id, @RequestBody ClassId request) {
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        // Retrieve the class by ID
        _Class _class = classRepo.findById(request.class_id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID:" + request.class_id));

        boolean alreadyEnrolled = student.getClasses().stream()
                .anyMatch(c -> c.getCourse().equals(_class.getCourse()));

        if (student.getClasses().contains(_class))
            throw new IllegalStateException("The student is already enrolled in this class");

        if (alreadyEnrolled) {
            throw new IllegalStateException("The student is already enrolled in another class of the same course");
        }
        // Add the class to the instructor's list of classes
        if (!student.getClasses().contains(_class) && _class.getRegistered() < _class.getCapacity()) {
            _class.getStudents().add(student);
            _class.setRegistered(_class.getRegistered() + 1);
            student.getClasses().add(_class);
            student.setTotalCreditHours(student.getTotalCreditHours() + _class.getCourse().getCreditHours());
        }
        else if (_class.getRegistered() >= _class.getCapacity())
            throw new IllegalStateException("This class is full");

        // Save the updated entities
        studentRepo.save(student);
        classRepo.save(_class);

        return ResponseEntity.ok("Student with ID: " + id + " joined the class with ID: " + request.class_id + " successfully");
    }

    // Get all classes joined by a student given ID
    @GetMapping("/{id}/classes")
    public List<ClassRecord> getAllClasses(@PathVariable Integer id) {
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        return student.getClasses().stream().map(_class -> new ClassRecord(
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
