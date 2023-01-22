package mk.ukim.finki.wp.kol2022.g2.service.impl;


import mk.ukim.finki.wp.kol2022.g2.model.Course;
import mk.ukim.finki.wp.kol2022.g2.model.Student;
import mk.ukim.finki.wp.kol2022.g2.model.StudentType;
import mk.ukim.finki.wp.kol2022.g2.model.exceptions.InvalidCourseIdException;
import mk.ukim.finki.wp.kol2022.g2.model.exceptions.InvalidStudentIdException;
import mk.ukim.finki.wp.kol2022.g2.repository.CourseRepository;
import mk.ukim.finki.wp.kol2022.g2.repository.StudentRepository;
import mk.ukim.finki.wp.kol2022.g2.service.StudentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {


    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public StudentServiceImpl(CourseRepository courseRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Student> listAll() {
        return this.studentRepository.findAll();
    }

    @Override
    public Student findById(Long id) {
        return this.studentRepository.findById(id).orElseThrow(InvalidStudentIdException::new);
    }

    @Override
    public Student create(String name, String email, String password, StudentType type, List<Long> courseId, LocalDate enrollmentDate) {
        List<Course> courses = this.courseRepository.findAllById(courseId);
        Student student = new Student(name,email,password,type,courses,enrollmentDate);
        return this.studentRepository.save(student);
    }

    @Override
    public Student update(Long id, String name, String email, String password, StudentType type, List<Long> coursesId, LocalDate enrollmentDate) {
        Student student = this.findById(id);
        student.setName(name);
        student.setEmail(email);
        student.setPassword(password);
        student.setType(type);
        List<Course> courses = this.courseRepository.findAllById(coursesId);
        student.setCourses(courses);
        student.setEnrollmentDate(enrollmentDate);
        return this.studentRepository.save(student);
    }

    @Override
    public Student delete(Long id) {
        Student student = this.findById(id);
        this.studentRepository.delete(student);
        return student;
    }

    @Override
    public List<Student> filter(Long courseId, Integer yearsOfStudying) {
        List<Student> students;
        if (courseId == null && yearsOfStudying == null) {
            students = studentRepository.findAll();
        } else if (courseId != null && yearsOfStudying != null) {
            students = studentRepository.findAllByCoursesContainingAndEnrollmentDateBefore(
                    this.courseRepository.findById(courseId).orElseThrow(InvalidCourseIdException::new), LocalDate.now().minusYears(yearsOfStudying));
        } else if (courseId != null) {
            students = studentRepository.findAllByCoursesContaining(this.courseRepository.findById(courseId).orElseThrow(InvalidCourseIdException::new));
        } else
            students = studentRepository.findAllByEnrollmentDateBefore(LocalDate.now().minusYears(yearsOfStudying));
        return students;

    }
}
