package edu.sjsu.cmpe275.lab2.model;

import java.util.ArrayList;
import java.util.List;

public class ReservationForGetPassengers {
	
	private String orderNumber;
	
    private int price; 
	
	private List<FlightForGetPassengers> flights;
    
	public ReservationForGetPassengers() {
	}
	
	public ReservationForGetPassengers(Reservation reservation) {
		this.price = reservation.getPrice();
		this.flights = new ArrayList<FlightForGetPassengers>();
		for(Flight flight:reservation.getFlights()){
			FlightForGetPassengers flightForGetPassengers = new FlightForGetPassengers(flight);
			this.flights.add(flightForGetPassengers);
		}
		this.orderNumber = reservation.getOrderNumber();
	}

	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public List<FlightForGetPassengers> getFlights() {
		return flights;
	}
	public void setFlights(List<FlightForGetPassengers> flights) {
		this.flights = flights;
	}
}

