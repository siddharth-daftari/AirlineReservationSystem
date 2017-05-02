package edu.sjsu.cmpe275.lab2.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;


@StaticMetamodel(Passenger.class)
public class Passenger_ {
	
	public static volatile SingularAttribute<Passenger, String> id;
	public static volatile SingularAttribute<Passenger, String> firstname;
	public static volatile SingularAttribute<Passenger, String> lastname;
	public static volatile SingularAttribute<Passenger, Integer> age;
	public static volatile SingularAttribute<Passenger, String> gender;
	public static volatile SingularAttribute<Passenger, String> phone;
	
}
