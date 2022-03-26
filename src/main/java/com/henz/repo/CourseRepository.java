package com.henz.repo;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.henz.entity.Course;
import com.henz.entity.Review;

@Repository
@Transactional //all methods
public class CourseRepository {
	
	private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

	@Autowired
	private EntityManager entityManager;
	
	public Course findById(Long id) {
		return entityManager.find(Course.class, id);
	}
	
	//transaction needed for delete
	public void deleteById(Long id) {
		Course course = this.findById(id);
		entityManager.remove(course);
	}
	
	public Course save(Course course) {
		if(course == null) {
			entityManager.persist(course);
		}else {
			entityManager.merge(course);
		}
		
		return course;
	}
	
	public void playWithEntitiyManager() {
		Course course = new Course("web services in 100 steps");
		entityManager.persist(course);
		entityManager.flush(); //send changed data until now to DB instead of waiting until end of method. but make a rollback if an exception occurs later in the method
		
		course.setName("web services in 100 steps updated"); //this is automatically saved by entity manager with help of @transactional
		
		Course course2 = new Course("angular in 100 steps");
		entityManager.persist(course2);
		entityManager.flush();
		
		//for no longer tracking course2 with entityManager, we can use detach. so the next change will not occur anymore because object is not tracked anymore
		entityManager.detach(course2); //it is also called "we detach the object from the PersistenceContext"
		
		course2.setName("angular in 100 steps updated"); //this will not be saved to db because of detach
		
		//instead of detach, we can clear everything which is tracked by entityManager
		//entityManager.clear();
		
		//make another change to course...
		course.setName("update again");
		entityManager.flush();
		
		//or we can reset all changes so far of an object by...
		entityManager.refresh(course); //but this does not reset the data already saved in db, only in java object --> if we would refresh course2 we would get an exception because it is detached
	}
	
	public void addReviewsForCourse() {
		//get course
		Course course = this.findById(1L); //this will fire a query for course
		logger.info("reviews of this course: -> {}",course.getReviews()); //get reviews will fire an additional query 'where course_id =?'
		
		
		//add review
		Review review1 = new Review("5", "rev1");
		Review review2 = new Review("5", "rev2");
		
		course.addReview(review1);
		course.addReview(review2);
		
		//...but this is not sufficient for saving the review, we also must add course on review side because review is owning side
		review1.setCourse(course);
		review2.setCourse(course);
		
		//now persist
		entityManager.persist(review2);
		entityManager.persist(review1);
		
	}
}
