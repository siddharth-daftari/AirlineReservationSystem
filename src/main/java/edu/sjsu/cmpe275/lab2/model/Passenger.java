package edu.sjsu.cmpe275.lab2.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="PASSENGER")
public class Passenger {
	@Id
	@Column(name="PASSENGER_ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;
	@Column(name="FIRSTNAME")
	private String firstname;
	@Column(name="LASTNAME")
	private String lastname;
	@Column(name="AGE")
	private int age;
	@Column(name="GENDER")
	private String gender;
	@Column(name="PHONE")
	private String phone; //must be unique
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	

}
