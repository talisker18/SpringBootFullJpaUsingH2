package com.henz;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.henz.entity.Course;
import com.henz.repo.CourseRepository;

@SpringBootApplication
public class SpringBootFullJpaUsingH2Application implements CommandLineRunner{
	
	private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CourseRepository courseRepository;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootFullJpaUsingH2Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/**
		 * see unit tests for more code
		 * 
		 * */
		
		/**
		 * 
		 * see unit tests and CourseRepository how @Transactional works and what happens 
		 * if we flush, detach, clear etc an entity manager (see CourseRepository for that)
		 * 
		 * see also package transaction for more info about transactions
		 * see unit tests for examples using transactions
		 * 
		 * ACID:
		 * 	atomicity -> commit all changes or rollback everything
		 * 	concurrent -> thousands of transactions in parallel
		 * 	isolation -> multiple level of isolation -> defining locks or not
		 * 	durability -> persistent
		 * 
		 * 
		 * 
		 * 
		 * */
		
		
		/**
		 * see folder entity for relationships, inheritance
		 * 
		 * see unit tests how to access entities in relationships
		 * 
		 * */
		
		
		/**
		 * see unit tests for different types of querying DB (jpql, native query, criteria query)
		 * 
		 * */
		
		/**
		 * see application.prop file for enabling h2 console, showing hibernate queries in console etc
		 * 
		 * 
		 * how to setup db initialization in spring:
		 * 
		 * Spring Boot can automatically create the schema (DDL scripts) of your DataSource and initialize it (DML scripts). It loads SQL from the standard root classpath locations: schema.sql and data.sql, respectively.
		 * 
		 * In addition, Spring Boot processes the schema-${platform}.sql and data-${platform}.sql files (if present), where platform is the value of spring.datasource.platform. This allows you to switch to database-specific scripts if necessary. For example, you might choose to set it to the vendor name of the database (hsqldb, h2, oracle, mysql, postgresql, and so on).
		 * 
		 * */
		
		
		/**
		 * if manyToMany relationship, make a join table. example: students and course is many to many
		 * 
		 * join table uses student_id and course_id
		 * 
		 * 
		 * */
		
		/**
		 * 
		 * this app shows also how to implement inheritance with hibernate
		 * 
		 * we can do this with following styles:
		 * 	single table
		 * 	table per class
		 * 	joined
		 * 	mapped super class
		 * 
		 * 
		 * we are using: abstract employee and 2 sub classes
		 * 
		 * 
		 * */
		
		
		/**
		 * see repo folder for a repo interface using spring data jpa (CourseSpringDataRepository)
		 * 
		 * --> the other repositories are just using jpa
		 * 
		 * */
		
		
		/**
		 * hibernate caching: see package caching and unit tests
		 * 
		 * 
		 * */
		
		
		/**
		 * how to use soft deletes: see tests
		 * 
		 * */
		
		/**
		 * instead of doing relationships between entities we can do it without relationships
		 * e.g. student has a field called Adress. the address class has mutliple fields. now we want to store this field directly in the student table
		 * 
		 * --> see Student.class and Adress.class. Use @Embeddable on Adress.class
		 * 
		 * */
		
		
		/**
		 * using enum as field: see Review.class, field rating
		 * 
		 * */
		
		/**
		 * performance tips database: see package performance and unit tests regarding n+1 problem
		 * 
		 * */
		
		
		/**
		 * how to easily switch database: 
		 * 	add dependency in pom
		 * 	config prop file
		 * 	...thats it
		 * 
		 * */
		
		
		/**
		 * further tips: use in memory db like h2 for unit tests, real db for the actual application
		 * 
		 * */
		
		//insert
		/*Course course = new Course("jpa in 50 steps");
		courseRepository.save(course);
		
		//to update, first we have to get the entity from database
		course = courseRepository.findById(1L);
		course.setName("jpa in 50 steps updated");
		courseRepository.save(course);*/
		
		//courseRepository.playWithEntitiyManager();
		
	}

}
