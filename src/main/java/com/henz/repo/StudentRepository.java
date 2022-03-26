package com.henz.repo;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.henz.entity.Course;
import com.henz.entity.Passport;
import com.henz.entity.Student;

@Repository
@Transactional //all methods
public class StudentRepository {
	
	private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

	@Autowired
	private EntityManager em;
	
	public Student findById(Long id) {
		return em.find(Student.class, id);
	}
	
	//transaction needed for delete
	public void deleteById(Long id) {
		Student student = this.findById(id);
		em.remove(student);
	}
	
	public Student save(Student student) {
		if(student == null) {
			em.persist(student);
		}else {
			em.merge(student);
		}
		
		return student;
	}
	
	public void saveStudentWithPassport() {
		Passport passport = new Passport("Z123456");
		//Student student = new Student("Mike");
		//student.setPassport(passport);
		//em.persist(student); //we get an exception here because we didnt save passport first
		//Student class is the owning side of the OneToOne relationship, so we have to save the passport first
		
		//so first save passport
		
		//hibernate is lazy. it will wait as long as it can before inserting the passport in DB
		//so it will save not when calling persist(), but when the whole transaction has finished
		em.persist(passport);
		
		Student student = new Student("Mike");
		student.setPassport(passport); 
		
		em.persist(student);
	} //after method, hibernates saves: 1st passport, 2nd student
	
	public void insertStudentAndCourse() {
		Student s = new Student("jack");
		Course c = new Course("course1");
		
		em.persist(s); //save student
		em.persist(c); //save course
		
		//add relationship
		s.addCourse(c);
		c.addStudent(s);
		
		//to save relationship into database, use em and persist the OWNING side -> student
		em.persist(s);
	}
}
