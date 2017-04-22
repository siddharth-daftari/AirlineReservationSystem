package edu.sjsu.cmpe275.lab2.model;



import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@Table(name="RESERVATION")
public class Reservation {
	/*@Id
	@Column(name="ORDER_NUMBER")
	@GeneratedValue(strategy=GenerationType.AUTO)*/
	@Id 
	@Column(name="ORDER_NUMBER")
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String orderNumber;
	@ManyToOne(optional=false)
	@JoinColumn(name="PASSENGER_ID")
	private Passenger passenger;
	
	@Column(name="PRICE")
    private int price; // sum of each flight’s price.
	
	@OneToMany(mappedBy="number")
    private List<Flight> flights;
    
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public Passenger getPassenger() {
		return passenger;
	}
	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public List<Flight> getFlights() {
		return flights;
	}
	public void setFlights(List<Flight> flights) {
		this.flights = flights;
	}

}
