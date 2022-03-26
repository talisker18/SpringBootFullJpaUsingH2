package com.henz.entity;

import javax.persistence.Embeddable;

@Embeddable //adress is used in Student.class as field. instead of doing relationship, we want the address field to be stored directly in student table
//this is usefull if we use Adress in other classes as well, not only in Student.class
public class Address {
	String street;
	int number;
	
	public Address() {
	}

	public Address(String street, int number) {
		super();
		this.street = street;
		this.number = number;
	}
	
	
}
