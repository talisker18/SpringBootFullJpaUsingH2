package com.henz.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.henz.entity.Course;

/**
 * Spring Data JPA has a built in query creation mechanism which can be used for parsing queries straight from the method name of a query method. 
 * This mechanism first removes common prefixes from the method name and parses the constraints of the query from the rest of the method name. The 
 * query builder mechanism is described with more details in Defining Query Methods Subsection of Spring Data JPA reference documentation.

Using this approach is quite simple. All you have to do is to ensure that the method names of your repository interface are created by combining the 
property names of an entity object and the supported keywords. The Query Creation Subsection of the Spring Data JPA reference documentation has nice 
examples concerning the usage of supported keywords.
 * 
 * 
 * */

/**
 * we can also annotate this repo to be used in a restful application as follows
 * 
 * */

@RepositoryRestResource(path="courses")
//now we can access all courses by hitting this path
//but in Course.class on the List<Student>, add @JsonIgnore to avoid infinite loop
public interface CourseSpringDataRepository extends JpaRepository<Course, Long>{
	//JpaRepository has some default CRUD operations, but we can define also own methods
	
	//search by name
	List<Course> findByName(String name); //or queryByName
	//or more criteria
	List<Course> findByNameAndId(String name, Long id);
	//do sorting
	List<Course> findByNameOrderByIdDesc(String name);
	
	//custom delete
	List<Course> deleteByName(String name);
	
	//with jpql
	@Query("Select c from Course c where name like '%100 steps%'")
	List<Course> courseWith100StepsInName();
	
	//with jpql
	@Query(value="Select * from course where name like '%100 steps%'", nativeQuery = true)
	List<Course> courseWith100StepsInNameNativeQuery();
	
	
}
