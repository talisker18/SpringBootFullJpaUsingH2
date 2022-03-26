package com.henz.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="passport")
public class Passport {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name="passport_number", nullable = false) //same as table, but  for column. and record may not be null. there are other constraints like updatable, insertable, max length etc
	private String passportNumber;
	
	//provided by hibernate...also change the schema!
	@UpdateTimestamp
	@Column(name="last_updated")
	private LocalDateTime lastUpdated; 
	@CreationTimestamp
	private LocalDateTime created;
	
	//if we do not declare a Student instace var here, the OneTOne relationship is called uni directional
	//if we add a Student here, it is called bi directional
	
	//if doing bi directional, make sure to define a owning side. if not, bot tables of the relationship will get a column for the relationship -> duplicate information!
	//if we want that the Student side is the owning side, do the following
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "passport") //use the variable name of 'passport' from the Student.class
	//--> always do this on the NOT owning side
	//--> this will generate a column in Student table with passport_id and NOT a student_id column in passport table
	Student student;
	
	public Passport() {
		
	}

	public Passport(String passportNumber) {
		super();
		this.passportNumber = passportNumber;
	}

	public Long getId() {
		return id;
	}

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
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

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	@Override
	public String toString() {
		return "Passport [id=" + id + ", passportNumber=" + passportNumber + ", lastUpdated=" + lastUpdated
				+ ", created=" + created + "]";
	}
}
