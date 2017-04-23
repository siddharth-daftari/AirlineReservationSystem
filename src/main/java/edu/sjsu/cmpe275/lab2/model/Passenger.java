package edu.sjsu.cmpe275.lab2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="PASSENGER")
public class Passenger {
	/*@Id
	@Column(name="PASSENGER_ID")
	@GeneratedValue(strategy=GenerationType.AUTO)*/
	//, unique = true, nullable = false
	
	@Id 
	@Column(name="PASSENGER_ID")
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String id;
	
	@Column(name="FIRSTNAME", nullable = false)
	private String firstname;
	@Column(name="LASTNAME", nullable = false)
	private String lastname;
	@Column(name="AGE", nullable = false)
	private int age;
	@Column(name="GENDER", nullable = false)
	private String gender;
	@Column(name="PHONE", unique = true, nullable = false)
	private String phone; //must be unique
	
	public Passenger() {
		
	}
	
	public Passenger(String firstname, String lastname, int age, String gender, String phone) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.age = age;
		this.gender = gender;
		this.phone = phone;
	}
	
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
