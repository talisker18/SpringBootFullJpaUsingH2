package com.henz.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity //bean that is stored in DB
@Table(name="course") //map to the name of table. if same as in DB we do not need to use this annotation
//see also Test class using jpql, jpqlDemoTestUsingNamedQuery() method. if we need more queries...
//@NamedQuery(name="query_get_all_courses", query="Select c From Course c")
@NamedQueries(value = {
		@NamedQuery(name="query_get_all_courses", query="Select c From Course c"),
		@NamedQuery(name="query_get_course_byId", query="Select c From Course c where id =1") //we cant use placeholders here like ? or %d
})
@Cacheable //add 2nd level cache, see also prop file
//now make a request and look at the logs. First time the select is done, the log says '...performing 1 L2C puts', meaning that data was put in cache...when query is fired again it says '... 0 puts' and '...1 L2C hits', meaning that the data was read from cache

//soft delete by hibernate
@SQLDelete(sql="update course set is_deleted=true where id=?")
@Where(clause = "is_deleted = false") //if courses are searched by a select, only return records with is_deleted = false. UNFORTUNATELY, this does not apply to native queries!!!
//pay attention: isDeleted instance var does not change if the soft delete is executed. so add a method which sets the boolean to true after a record is soft deleted. add @PreRemove to this method
public class Course {
	
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
	
	//also change db schema
	@OneToMany(mappedBy = "course") //Review is the owning side of the relationship
	private List<Review> reviews = new ArrayList<Review>(); //go to Review.class and get the instance var name of Course
	
	//making join table
	//this will create course_students join table
	//make one of this relation the owning side to only get 1 join table
	//it doesnt matter which side
	@ManyToMany(mappedBy = "courses") //Student is owning side, so there will be table student_courses
	@JsonIgnore // --> see CourseSpringDataRepository. we have to do this because we have manytomany relation
	List<Student> students = new ArrayList<Student>();
	
	private boolean isDeleted; //soft delete: dont delete the record but set this to true
	
	//default constructor necessary for jpa
	public Course() {
		
	}
	
	public Course(String name) {
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

	public List<Review> getReviews() {
		return reviews;
	}

	public void addReview(Review review) {
		this.reviews.add(review);
	}
	
	public void removeReview(Review review) {
		this.reviews.remove(review);
	}

	public List<Student> getStudents() {
		return students;
	}

	//instead of using setter, use adder and remove method
	public void addStudent(Student students) {
		this.students.add(students);
	}
	
	@PreRemove //whenever the soft delete is fired, set isDeleted to true
	//there are other hooks like PostPersist, PostRemove, PostUpdate etc or PreUpdate etc
	private void preRemove() {
		this.isDeleted = true;
	}

	@Override
	public String toString() {
		return "Course [id=" + id + ", name=" + name + "]";
	}
}
