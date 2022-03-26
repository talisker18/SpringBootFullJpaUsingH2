package com.henz;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.henz.entity.Course;
import com.henz.entity.Passport;
import com.henz.entity.Review;
import com.henz.entity.Student;
import com.henz.entity.employee.FullTimeEmployee;
import com.henz.entity.employee.PartTimeEmployee;
import com.henz.repo.CourseRepository;
import com.henz.repo.CourseSpringDataRepository;
import com.henz.repo.EmployeeRepository;
import com.henz.repo.StudentRepository;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Subgraph;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;


//unit tests are run between context launch and destroy
@SpringBootTest(classes=SpringBootFullJpaUsingH2Application.class) //load the entire application context into test env
class SpringBootFullJpaUsingH2ApplicationTests {
	
	private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private CourseSpringDataRepository springDataCourseRepo;
	
	@Autowired
	EntityManager entityManager;

	@Test
	@DirtiesContext //after test is run, the changes of data (delete, insert) are rolled back
	void someTest() {
		
		//before the test is run, the CommandLineRunner from the app is also run
		//so comment the body of CommandLineRunner out to run the tests
		
		//insert
		courseRepository.save(new Course("jpa in 50 steps")); //id 1L
		
		//find
		Course course = this.courseRepository.findById(1L);
		assertEquals("jpa in 50 steps", course.getName());
		
		//update
		//to update, first we have to get the entity from database. we did this already on line 34
		course.setName("jpa in 50 steps updated");
		courseRepository.save(course);
		course = courseRepository.findById(1L);
		assertEquals("jpa in 50 steps updated", course.getName());
		
		//delete
		courseRepository.deleteById(1L);
		assertNull(courseRepository.findById(1L));
	}
	
	@Test
	@DirtiesContext
	public void playWithEntityManager() {
		courseRepository.playWithEntitiyManager();
	}
	
	@Test
	//@Transactional
	public void testStudentsUsingEntityManager() {
		//manually insert some data of students and passport
		Student student = entityManager.find(Student.class, 1L); //when getting the student, hibernate does left join on passport automatically because of OneToOne -> OneToOne is always eager fetch by default
		//meaning that the other side of the relationship is also loaded by doing JOINS
		System.out.println(student.getPassport()); //eager fetch -> data is available
		
		
		//if we do not want to load passport data when asking for students: make Passport passport var in Student.class as follows:
		//@OneToOne(fetch=FetchType.LAZY) -> it will do a select without join
		
		//if we will do that and asking for passport data, then it will throw a exception
		//to avoid exception in this case, we have to add @Transactional over testStudentsUsingEntityManager() test method
		//in this case, hibernate will do a separate select * from passport after fetching the student data ()
		
		//-> so best for performance (?): do everywhere lazy fetching with @Transactional?
	}
	
	@Test
	@Transactional //Persistence Context -> in hibernate, session = Persistence Context
	public void testStudentsUsingEntityManagerMultipleDbOperations() {
		//DB operation 1: retrieve student
		Student student = entityManager.find(Student.class, 1L);
		//Persistence Context = student
		
		//DB operation 2: retrieve passport
		Passport passport = student.getPassport();
		//Persistence Context = student + passport -> we do not have to use entityManager for this thanks to @Transaction!! without @Transaction it would throw an exception
		
		//DB operation 3: update passport
		passport.setPassportNumber("E123456 - updated");
		//Persistence Context = student + passport changed
		
		//DB operation 4: update student
		student.setName("mike - updated");
		//Persistence Context = student changed + passport changed
	} //send changes to DB here, at end of method
	
	@Test
	@Transactional
	public void getStudentInfoWithHelpOfPassport() {
		//this will be possible if we do a bi directional relationship between student and passport
		Passport pass = entityManager.find(Passport.class, 1L);
		System.out.println(pass.getStudent());
	}
	
	
	@Test
	@Transactional //needed to get lazy fetched reviews from course
	public void retrieveReviewsForCourse() {
		Course course = courseRepository.findById(1L);
		logger.info("reviews of this course: -> {}",course.getReviews()); //by default, *toMany relationships (as in Course.class for reviews) is lazy on fetch type...this will throw exception without transactional
		
		//we also could use eager fetch type...but be careful, this could be bad for performance
		
	}
	
	@Test
	@Transactional
	public void retrieveCourseFromReview() {
		Review review = this.entityManager.find(Review.class, 1L);
		logger.info("course of this review: -> {}",review.getCourse()); //from review to course its *toOne. default its eager fetching, so better change it to lazy on review side
		
	}
	
	@Test
	@Transactional // *toMany is lazy on default, so use @transactional...in this case its not necessary to annotate the test method because we are using studentRepo
	public void retrieveStudentAndHisCourses() {
		Student s = studentRepository.findById(1L);
		
		//get courses of this student
		logger.info("courses of this student: -> {}",s.getCourses()); //fire a join query to get the details of this student
	}
	
	@Test
	public void insertEmployee() {
		this.employeeRepository.insert(new PartTimeEmployee("joel", new BigDecimal("50")));
		
		this.employeeRepository.insert(new FullTimeEmployee("joel", new BigDecimal("10000")));
		
		
	}
	
	
	/**
	 * JPQL
	 * 
	 * */
	
	@Test
	public void jpqlDemoTest() {
		List result = entityManager.createQuery("Select c From Course c").getResultList(); //same as select * from
		
		//typed...always better to do this
		TypedQuery<Course> query = entityManager.createQuery("Select c From Course c", Course.class);
		List<Course> result2 = query.getResultList();
		
		//antoher query
		query = entityManager.createQuery("Select c From Course c where name like '%100%'", Course.class);
	}
	
	@Test
	public void jpqlDemoTestUsingNamedQuery() {
		//insert query here
		TypedQuery<Course> query = entityManager.createQuery("Select c From Course c where name like '%100%'", Course.class);
		
		//used named query
		query = entityManager.createNamedQuery("query_get_all_courses", Course.class);
		
		query = entityManager.createNamedQuery("query_get_course_byId", Course.class);
	}
	
	//select * from course where course.id not in (select course_id from student_course);
	@Test
	public void jpql_findAllCoursesWithoutAnyStudent() {
		TypedQuery<Course> query = entityManager.createQuery("select c from Course c where c.students is empty",Course.class);
		
		List<Course> courseList = query.getResultList();
	}
	
	@Test
	public void jpql_findAllCoursesWithAtLeastTwoStudents() {
		TypedQuery<Course> query = entityManager.createQuery("select c from Course c where size(c.students) >= 2",Course.class);
		
		List<Course> courseList = query.getResultList();
	}
	
	@Test
	public void jpql_findAllCoursesOrderedByStudents() {
		TypedQuery<Course> query = entityManager.createQuery("select c from Course c order by size(c.students) desc",Course.class);
		
		List<Course> courseList = query.getResultList();
	}
	
	//find students whose passport contain '1234'
	//or use other functions like:
	// BETWEEN 100 and 1000
	// IS NULL
	@Test
	public void jpql_findStudentsWhosePoassportContaint1234() {
		TypedQuery<Student> query = entityManager.createQuery("Select s from Student where s.passport.number like '%1234%'",Student.class);
		
		List<Student> courseList = query.getResultList();
	}
	
	//JOIN -> Select c, s from Course c JOIN c.students s -> with this we wont get the courses without students
	//LEFT JOIN --> returns also Courses without assigned students
	//CROSS JOIN -> Select c, s from Course c, Student s
	@Test
	public void jpql_join() {
		//we cannot use typed query in joins
		Query query = entityManager.createQuery("Select c, s from Course c JOIN c.students s");
		
		List<Object[]> resultList = query.getResultList();
		
		for(Object[] result: resultList) {
			System.out.println(result[0]); //index 0 is Course because we use it in first place in the query
			System.out.println(result[1]); //student
		}
		
	}
	
	/**
	 * native query
	 * 
	 * */
	
	@Test
	public void nativeQuery() {
		Query query = entityManager.createNativeQuery("select * from course where id=?", Course.class); //map it to class
		query.setParameter(1, 1L); //get id 1
		
		//or with named param
		query = entityManager.createNativeQuery("select * from course where id= :id", Course.class); //map it to class
		query.setParameter("id", 1L); //get id 1
	}
	
	@Test
	@Transactional
	//remeber: for this we need a transaction. we are using entityManager here directly without courseRepository
	public void updateWithQuery() {
		//update all records, native query
		Query query = entityManager.createNativeQuery("update course set name = 'someCourse'", Course.class); //map it to class
		int rows = query.executeUpdate();
		
		TypedQuery<Course> typedQuery = entityManager.createQuery("UPDATE course SET name = :name WHERE id = :id", Course.class);
		typedQuery.setParameter("name", "someCourseName");
		typedQuery.setParameter("id", 1L);
		
	}
	
	/**
	 * criteria queries, use pure Java
	 * 
	 * 
	 * */
	
	@Test
	public void criteriaQueryWithoutConditions() {
		//select c From Course c
		
		//1. use criteria builder to create criteria query returning the expected result object
		CriteriaBuilder cb= entityManager.getCriteriaBuilder();
		CriteriaQuery<Course> cq = cb.createQuery(Course.class);
		
		//2. define roots fro tables which are involved in the query
		Root<Course> root = cq.from(Course.class);
		
		//3. define predicates etc using criteria builder
		
		//4. add predicates etc to the criteria query
		
		//5. build the typedQuery using entity manager and criteria query
		TypedQuery<Course> query = entityManager.createQuery(cq.select(root));
		
		List<Course> courseList = query.getResultList();
	}
	
	@Test
	public void criteriaQueryWithConditions() {
		//select c From Course c where name like '%100 steps'
		
		//1. use criteria builder to create criteria query returning the expected result object
		CriteriaBuilder cb= entityManager.getCriteriaBuilder();
		CriteriaQuery<Course> cq = cb.createQuery(Course.class);
		
		//2. define roots for tables which are involved in the query
		Root<Course> root = cq.from(Course.class);
		
		//3. define predicates etc using criteria builder
		Predicate like = cb.like(root.get("name"), "%100 steps");
		
		//4. add predicates etc to the criteria query
		cq.where(like);
		
		//5. build the typedQuery using entity manager and criteria query
		TypedQuery<Course> query = entityManager.createQuery(cq.select(root));
		
		List<Course> courseList = query.getResultList();
	}
	
	@Test
	public void criteriaQuery_allCoursesWithoutStudents() {
		//select c From Course c where c.students is empty
		
		//1. use criteria builder to create criteria query returning the expected result object
		CriteriaBuilder cb= entityManager.getCriteriaBuilder();
		CriteriaQuery<Course> cq = cb.createQuery(Course.class);
		
		//2. define roots for tables which are involved in the query
		Root<Course> root = cq.from(Course.class);
		
		//3. define predicates etc using criteria builder
		Predicate isEmpty = cb.isEmpty(root.get("students")); //c.students
		
		//4. add predicates etc to the criteria query
		cq.where(isEmpty);
		
		//5. build the typedQuery using entity manager and criteria query
		TypedQuery<Course> query = entityManager.createQuery(cq.select(root));
		
		List<Course> courseList = query.getResultList();
	}
	
	@Test
	public void criteriaQuery_join() {
		//select c From Course c join c.students s
		
		//1. use criteria builder to create criteria query returning the expected result object
		CriteriaBuilder cb= entityManager.getCriteriaBuilder();
		CriteriaQuery<Course> cq = cb.createQuery(Course.class);
		
		//2. define roots for tables which are involved in the query
		Root<Course> root = cq.from(Course.class);
		//join
		//for left join -> root.join("students", JoinType.LEFT);
		Join<Object,Object> join = root.join("students"); //c.students
		
		//3. define predicates etc using criteria builder
		
		//4. add predicates etc to the criteria query
		
		//5. build the typedQuery using entity manager and criteria query
		TypedQuery<Course> query = entityManager.createQuery(cq.select(root));
		
		List<Course> courseList = query.getResultList();
	}
	
	/**
	 * transaction management
	 * 
	 * @TRansactional from javax.transaction -> only for single databases
	 * 
	 * --> if you need transaction management across mutliple databases, use @Transaction from spring
	 * 
	 * --> so better just use always spring
	 * 
	 * 
	 * */
	
	@Test
	@Transactional(isolation = Isolation.READ_COMMITTED)
	//you can also set isolation level globally in prop file: spring.jpa.properties.hibernate.connection.isolation=2
	// --> 1 is read uncommitted, 2 is read committed, 4 is repeatable read and 8 is serializable
	public void transaction_test() {
		
	}
	
	
	/**
	 * transaction propagation
	 * 
	 * REQUIRED is the default propagation. Spring checks if there is an active transaction, and if nothing exists, it creates a new one. Otherwise, the business logic appends to the currently active transaction
	 * 
	 * For SUPPORTS, Spring first checks if an active transaction exists. If a transaction exists, then the existing transaction will be used. If there isn't a transaction, it is executed non-transactional:
	 * 
	 * When the propagation is MANDATORY, if there is an active transaction, then it will be used. If there isn't an active transaction, then Spring throws an exception
	 * 
	 * For transactional logic with NEVER propagation, Spring throws an exception if there's an active transaction:
	 * 
	 * NOT_SUPPORTED : If a current transaction exists, first Spring suspends it, and then the business logic is executed without a transaction:
	 * 
	 * When the propagation is REQUIRES_NEW, Spring suspends the current transaction if it exists, and then creates a new one:
	 * */
	
	@Test
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public void transaction_test2() {
		
	}
	
	
	/**
	 * spring data jpa. with sorting and pagination
	 * 
	 * 
	 * */
	@Test
	public void findById() {
		Optional<Course> course = this.springDataCourseRepo.findById(1L);
		logger.info("{}", course.isPresent());
	}
	
	@Test
	public void playWithSpringDataRepo() {
		Course course = new Course("course1");
		this.springDataCourseRepo.save(course);
		course.setName("course2");
		
		this.springDataCourseRepo.save(course); //use this to update
		
		logger.info("{}", this.springDataCourseRepo.findAll());
		logger.info("count all courses: {}", this.springDataCourseRepo.count()); //from the CrudRepository
		
		//do sorting
		logger.info("{}", this.springDataCourseRepo.findAll(
				Sort.by(
						Sort.Direction.DESC,
						new String[] {"name","id"}) //first sort by order, then by id
				));
		
		
		//pagination
		/*
		 * Whenever we want to load only a slice of a full list of items, we can use a Pageable instance as an input parameter, as it provides the number of the page to load as well as the size of the pages
		 * 
		 * */
		
		//get the first page, containing 2 elements
		
		//for this we need our springDataCourseRepo to extend PagingAndSortingRepository
		/*
		 * Pageable firstPageWithTwoElements = (Pageable) PageRequest.of(0, 2);
		Page<Course> page1 = this.springDataCourseRepo.findAll(firstPageWithTwoElements);
		 * 
		 * */
	
	}
	
	/**
	 * caching
	 * 
	 * 
	 * */
	
	@Test
	@Transactional //default 1st level cache
	public void test_firstLevelCache() {
		Course course = this.courseRepository.findById(1L); //query the database
		
		//do some other things
		
		course = this.courseRepository.findById(1L); //find again. this time it is loaded from 1st level cache. see the logging
		
	}
	
	//2nd level cache -> add dependency eh cache
	//then config the cache in prop file
	
	/**
	 * soft deletes. go to course class and add boolean isDeleted
	 * for each row isDeleted is false at the beginning
	 * 
	 * */
	// --> see Course.class, added @SqlDelete
	
	
	/**
	 * n+1 problem database joins
	 * 
	 * */
	@Test
	@Transactional
	public void test_NPlus1Problem() { //n+1 problem
		//we get all courses and want to print the students for each course
		List<Course> courses = entityManager.createQuery("Select c From Course c",Course.class).getResultList();
		
		for(Course course: courses) {
			logger.info("Course -> {} , Student {}", course, course.getStudents());
		}
		
		//what happens: fire 1 query to get all courses. for each course, fire 1 query to get the students. so we need to fire n+1 queries to get all info, whereas n = number of courses because for each course this query is fired: select ... join ... where student.course_id = ?
		
		//-->easy way to fix this: use eager fetch...but this will always get the students -> maybe performance problem
		//--> better way: adding a subgraph to the query. this way, default we use lazy fetch, but for this single query we add subgraph and getting kinda eager fetch
		
		EntityGraph<Course> entityGraph = this.entityManager.createEntityGraph(Course.class);
		//the subgraph is the student
		Subgraph<Object> subGraph = entityGraph.addSubgraph("students"); //'students' is the field name in the course class
		
		List<Course> courses2 = entityManager
				.createQuery("Select c From Course c",Course.class)
				.setHint("javax.persistence.loadgraph", entityGraph)
				.getResultList();
		
		//other solution: join fetch using JPQL
		List<Course> courses3 = entityManager
				.createQuery("Select c From Course c JOIN FETCH c.students s",Course.class)
				.getResultList();
		
		
	}
}
