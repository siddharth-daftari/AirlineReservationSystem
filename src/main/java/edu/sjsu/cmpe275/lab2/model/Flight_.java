package edu.sjsu.cmpe275.lab2.model;

import java.util.Date;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Flight.class)
public class Flight_ {
		
	public static volatile SingularAttribute<Flight, String> number;
	   	public static volatile SingularAttribute<Flight, Integer> price;
		public static volatile SingularAttribute<Flight, String> from;
	    public static volatile SingularAttribute<Flight, String> to;
	    public static volatile SingularAttribute<Flight, Date> departureTime;
		public static volatile SingularAttribute<Flight, Date> arrivalTime;
	    public static volatile SingularAttribute<Flight, Integer> seatsLeft;
	    public static volatile SingularAttribute<Flight, String> description;
	    public static volatile SingularAttribute<Flight, Plane> plane;
	    public static volatile ListAttribute<Flight, Passenger> passengers;
	   
	    
}
