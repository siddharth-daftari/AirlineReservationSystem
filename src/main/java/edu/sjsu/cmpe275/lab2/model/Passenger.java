package edu.sjsu.cmpe275.lab2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * @author siddharth and parvez
 *
 */
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
	
	/**
	 * 
	 */
	public Passenger() {
		
	}
	
	/**
	 * @param firstname
	 * @param lastname
	 * @param age
	 * @param gender
	 * @param phone
	 */
	public Passenger(String firstname, String lastname, int age, String gender, String phone) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.age = age;
		this.gender = gender;
		this.phone = phone;
	}
	
	/**
	 * @return
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return
	 */
	public String getFirstname() {
		return firstname;
	}
	/**
	 * @param firstname
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	/**
	 * @return
	 */
	public String getLastname() {
		return lastname;
	}
	/**
	 * @param lastname
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	/**
	 * @return
	 */
	public int getAge() {
		return age;
	}
	/**
	 * @param age
	 */
	public void setAge(int age) {
		this.age = age;
	}
	/**
	 * @return
	 */
	public String getGender() {
		return gender;
	}
	/**
	 * @param gender
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}
	/**
	 * @return
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	

}
