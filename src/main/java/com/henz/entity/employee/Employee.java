package com.henz.entity.employee;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
//we can use different inheritance strategies
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //this creates one table Employee. full and parttime emp are inserted in this table. fulltime has null in column hourly vage, parttime has null in salary
// --> this is a bit ugly with null values
// --> in the single table there will be also saved the type of employee in a new column. there we will save 'FullTimeEmployee' or 'PartTimeEmployee'
// --> we can define the name of this column as follows:
@DiscriminatorColumn(name = "employee_type")

//other type: TABLE_PER_CLASS...@DiscriminatorColumn not needed
// --> create table for both subclasses. the id in the subclass table references the id of the employee table
//--> if we retrieve all employes, a select with union selects is fired
// --> problem: repeated values, like name -  name is in both sub tables

//other type: JOINED 
// --> own tables for Employee, full and part
// --> to retrieve all employees, a join is fired
// --> table employee has ID, NAME
// --> fulltime table has SALARY, ID (fk to employee table)
//--> parttime table has HOURLYVAGE, ID (fk to employee table)
// --> possible problem: performance if there are many subclasses

//other type: dont use inheritance, use @MappedSupperclass instead of @Inheritance
// --> for this we have to remove @Entity in Employee.class too
// --> mappings occur only to subclasses
// --> now to retrieve all employees we have to define two methods: retrieveAllPartTimeEmployees and retrieveAllFullTimeEmployees
// --> this will generate 2 tables, no Employee table


public abstract class Employee {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	public Employee() {
		
	}
	
	public Employee(String name) {
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

	
}
