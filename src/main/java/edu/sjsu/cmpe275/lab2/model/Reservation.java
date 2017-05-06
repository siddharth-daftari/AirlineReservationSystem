package edu.sjsu.cmpe275.lab2.model;



import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * @author siddharth and parvez
 *
 */
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
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(name="PASSENGER_ID")
	private Passenger passenger;
	
	@Column(name="PRICE")
    private int price; // sum of each flight’s price.
	
	@ManyToMany()
	@JoinTable(name = "RESERVATION_FLIGHT", joinColumns = { @JoinColumn(name = "ORDER_NUMBER") }, inverseJoinColumns = { @JoinColumn(name = "FLIGHT_NUMBER") })
    private List<Flight> flights;
    
	/**
	 * 
	 */
	public Reservation() {
	}
	
	/**
	 * @param passenger
	 * @param price
	 * @param flights
	 */
	public Reservation(Passenger passenger, int price, List<Flight> flights) {
		super();
		this.passenger = passenger;
		this.price = price;
		this.flights = flights;
	}

	/**
	 * @return
	 */
	public String getOrderNumber() {
		return orderNumber;
	}
	/**
	 * @param orderNumber
	 */
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	/**
	 * @return
	 */
	public Passenger getPassenger() {
		return passenger;
	}
	/**
	 * @param passenger
	 */
	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}
	/**
	 * @return
	 */
	public int getPrice() {
		return price;
	}
	/**
	 * @param price
	 */
	public void setPrice(int price) {
		this.price = price;
	}
	/**
	 * @return
	 */
	public List<Flight> getFlights() {
		return flights;
	}
	/**
	 * @param flights
	 */
	public void setFlights(List<Flight> flights) {
		this.flights = flights;
	}
}

