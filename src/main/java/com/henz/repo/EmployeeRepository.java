package com.henz.repo;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.henz.entity.employee.Employee;

@Repository
@Transactional
public class EmployeeRepository {
	
	private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

	@Autowired
	private EntityManager entityManager;
	
	public void insert(Employee e) {
		entityManager.persist(e);
	}
	
	public List<Employee> retrieveAllEmployees(){
		//jpql
		return entityManager.createQuery("select e from Employee e", Employee.class).getResultList();
	}

}
