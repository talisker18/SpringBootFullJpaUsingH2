package com.henz.repo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.henz.SpringBootFullJpaUsingH2Application;
import com.henz.entity.Course;

//unit tests are run between context launch and destroy

/**
 * 
 * see SpringBootFullJpaUsingH2ApplicationTests for more tests
 * 
 * */


@SpringBootTest(classes=SpringBootFullJpaUsingH2Application.class) //load the entire application context into test environment
class CourseRepositoryTest {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
	
	//unit tests are run between context launch and destroy when the tests are executed
	//
	
	@Autowired
	CourseRepository repo;
	
	@Test
	void findByIdTest() {
		logger.info("unit tests are run");
		
		//insert first here
		
		Course course = repo.findById(1L);
		assertEquals("jpa in 50 steps", course.getName());
		
	}

}
