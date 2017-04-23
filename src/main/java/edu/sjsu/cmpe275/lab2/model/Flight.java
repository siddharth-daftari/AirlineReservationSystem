package edu.sjsu.cmpe275.lab2.model;

import java.util.Date;
import java.util.List;

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

@Entity
@Table(name="FLIGHT")
public class Flight {
		/*@Id
		@Column(name="FLIGHT_NUMBER")*/
		@Id 
		@Column(name="FLIGHT_NUMBER")
		
	   	private String number; // Each flight has a unique flight number.
		
		@Column(name="PRICE")
		private int price;
		@Column(name="SOURCE")
	    private String from;
		@Column(name="DESTINATION")
	    private String to;

	    /*  Date format: yy-mm-dd-hh, do not include minutes and sceonds.
	    ** Example: 2017-03-22-19
	    The system only needs to supports PST. You can ignore other time zones.  */
		@Column(name="DEPARTURE_TIME")
		private Date departureTime;
		@Column(name="ARRIVAL_TIME")
	    private Date arrivalTime;
		@Column(name="SEATS_LEFT")
	    private int seatsLeft; 
		@Column(name="DESCRIPTION")
	    private String description;
		

		//@JoinColumn(name="PLANE_ID")
		@Embedded		
	    private Plane plane;  // Embedded
	     
		public Flight() {
			super();
		}
		@ManyToMany()
		@JoinTable(name = "FLIGHT_PASSENGER", joinColumns = { @JoinColumn(name = "FLIGHT_NUMBER") }, inverseJoinColumns = { @JoinColumn(name = "PASSENGER_ID") })
		private List<Passenger> passengers;
		
		public Flight(String number, int price, String from, String to, Date departureTime, Date arrivalTime,
				int seatsLeft, String description, Plane plane, List<Passenger> passengers) {
			super();
			this.number = number;
			this.price = price;
			this.from = from;
			this.to = to;
			this.departureTime = departureTime;
			this.arrivalTime = arrivalTime;
			this.seatsLeft = seatsLeft;
			this.description = description;
			this.plane = plane;
			this.passengers = passengers;
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
		public List<Passenger> getPassengers() {
			return passengers;
		}
		public void setPassengers(List<Passenger> passengers) {
			this.passengers = passengers;
		}
	    
}
