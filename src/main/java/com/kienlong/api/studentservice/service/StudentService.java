package com.kienlong.api.studentservice.service;

import com.kienlong.api.studentservice.entity.Student;
import com.kienlong.api.studentservice.repo.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    StudentRepository repository;

    @Cacheable(value = "students")
    public List<Student> listStudent() {
        return repository.findAll();
    }

    @Cacheable(value = "student", key = "#id")
    public Student getStudent(Integer id){
        return repository.findStudentById(id);
    }

    @CachePut(value = "student", key = "#student.id")
    public Student addStudent(Student student) {
        return repository.save(student);
    }

    @CachePut(value = "student", key = "#student.id")
    public Student updateStudent(Student student) {
        Optional<Student> studentInDB = repository.findById(student.getId());
        if (studentInDB.isEmpty()) {
            return null;
        }
        student.setCreatedDate(studentInDB.get().getCreatedDate());
        student.setCreatedBy(studentInDB.get().getCreatedBy());
        student = repository.save(student);

        return student;
    }

    @CacheEvict(value = "student", key = "#id")
    public boolean deleteStudent(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
