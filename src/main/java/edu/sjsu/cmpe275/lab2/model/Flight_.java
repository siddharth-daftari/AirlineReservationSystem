package edu.sjsu.cmpe275.lab2.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import org.hibernate.annotations.GenericGenerator;

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
