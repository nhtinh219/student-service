package com.kienlong.api.studentservice.repo;

import com.kienlong.api.studentservice.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    Student findStudentById(Integer id);
}
