package com.henz.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="review")
public class Review {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name="description", nullable = true) //here we choose nullable
	private String description;
	
	
	private String rating; //nullable also true on default
	
	//or better do enum here
	@Enumerated(EnumType.STRING)
	private ReviewRating reviewRating;
	
	//provided by hibernate...also change the schema!
	@UpdateTimestamp
	@Column(name="last_updated")
	private LocalDateTime lastUpdated; 
	@CreationTimestamp
	private LocalDateTime created;
	
	//Review is the owning side, so it will have a column with course_id...dont forget to add fk constraint
	@ManyToOne
	private Course course;
	
	public Review() {
		
	}

	public Review(String description, String rating) {
		super();
		this.description = description;
		this.rating = rating;
	}

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	@Override
	public String toString() {
		return "Review [id=" + id + ", description=" + description + ", rating=" + rating + ", lastUpdated="
				+ lastUpdated + ", created=" + created + "]";
	}
}


