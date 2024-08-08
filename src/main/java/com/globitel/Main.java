package com.globitel;

import com.globitel.repos.InstructorRepo;
import com.globitel.repos.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Main {

    @Autowired
    private InstructorRepo instructorRepo;
    @Autowired
    private StudentRepo studentRepo;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    public boolean emailExists(String email){
        return (studentRepo.findByEmail(email).isPresent() || instructorRepo.findByEmail(email).isPresent());
    }

    public boolean phoneExists(String phone){
        return (studentRepo.findByPhone(phone).isPresent() || instructorRepo.findByPhone(phone).isPresent());
    }

}
