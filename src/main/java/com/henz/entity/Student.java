package com.henz.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="student")
public class Student {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name="name", nullable = false) //same as table, but  for column. and record may not be null. there are other constraints like updatable, insertable, max length etc
	private String name;
	
	//provided by hibernate...also change the schema!
	@UpdateTimestamp
	@Column(name="last_updated")
	private LocalDateTime lastUpdated; 
	@CreationTimestamp
	private LocalDateTime created;
	
	@OneToOne //also change data.sql script, add constraint. 
	//if we do bi directional relationship with passport, decide whether to fetch eager or lazy. by default, oneToOne is always eager
	private Passport passport;
	
	//making join table
	//this will create student_courses join table
	//make one of this relation the owning side to only get 1 join table
	//it doesnt matter which side
	//we choose student to be owning side. here we can define name of table and columns, see @JointTable
	//this should generate automatic table and the fk constraints
	@ManyToMany
	@JoinTable(
			name="student_course",
			joinColumns = @JoinColumn(name="student_id"),
			inverseJoinColumns = @JoinColumn(name="course_id")
			)
	List<Course> courses = new ArrayList<Course>();
	
	@Embedded
	private Address adress;
	
	public Student() {
		
	}

	public Student(String name) {
		super();
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(LocalDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public Passport getPassport() {
		return passport;
	}

	public void setPassport(Passport passport) {
		this.passport = passport;
	}

	public List<Course> getCourses() {
		return courses;
	}

	//instead of using setter, use adder and remove method
	public void addCourse(Course course) {
		this.courses.add(course);
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", lastUpdated=" + lastUpdated + ", created=" + created + "]";
	}

}
