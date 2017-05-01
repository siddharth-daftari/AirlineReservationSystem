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

import org.hibernate.annotations.GenericGenerator;

public class FlightForGetPassengers {
	
	   	private String number; // Each flight has a unique flight number.
		
		private int price;
		
	    private String from;
		
	    private String to;
		
		private Date departureTime;
		
	    private Date arrivalTime;
		
	    private int seatsLeft; 
		
	    private String description;
		
	    private Plane plane;  // Embedded
	     
		public FlightForGetPassengers() {
			super();
		}
		
		public FlightForGetPassengers(Flight flight) {
			
			this.number = flight.getNumber();
			this.price = flight.getPrice();
			this.from = flight.getFrom();
			this.to = flight.getTo();
			this.departureTime = flight.getDepartureTime();
			this.arrivalTime = flight.getArrivalTime();
			this.seatsLeft = flight.getSeatsLeft();
			this.description = flight.getDescription();
			this.plane = flight.getPlane();
		}
		public String getNumber() {
			return number;
		}
		public void setNumber(String number) {
			this.number = number;
		}
		public int getPrice() {
			return price;
		}
		public void setPrice(int price) {
			this.price = price;
		}
		public String getFrom() {
			return from;
		}
		public void setFrom(String from) {
			this.from = from;
		}
		public String getTo() {
			return to;
		}
		public void setTo(String to) {
			this.to = to;
		}
		public Date getDepartureTime() {
			return departureTime;
		}
		public void setDepartureTime(Date departureTime) {
			this.departureTime = departureTime;
		}
		public Date getArrivalTime() {
			return arrivalTime;
		}
		public void setArrivalTime(Date arrivalTime) {
			this.arrivalTime = arrivalTime;
		}
		public int getSeatsLeft() {
			return seatsLeft;
		}
		public void setSeatsLeft(int seatsLeft) {
			this.seatsLeft = seatsLeft;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Plane getPlane() {
			return plane;
		}
		public void setPlane(Plane plane) {
			this.plane = plane;
		}
	    
}
