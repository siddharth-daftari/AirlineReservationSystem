package edu.sjsu.cmpe275.lab2.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.cmpe275.lab2.dao.FlightDAO;
import edu.sjsu.cmpe275.lab2.dao.PassengerDAO;
import edu.sjsu.cmpe275.lab2.dao.ReservationDAO;
import edu.sjsu.cmpe275.lab2.model.Flight;
import edu.sjsu.cmpe275.lab2.model.Passenger;
import edu.sjsu.cmpe275.lab2.model.Reservation;

@RestController
public class ReservationController {
	@Autowired
	private ReservationDAO reservationDAO;
	@Autowired
	private FlightDAO flightDAO;
	//Reservation reservation = null;
	@Autowired
	private PassengerDAO passengerDAO;
	
	@RequestMapping(value="/reservation",method=RequestMethod.POST)
    public @ResponseBody Reservation createReservation(@RequestParam(value="passengerId") String passengerId, @RequestParam(value="flightLists") String[] flightList) {
		Reservation reservation = null;
		try {
			Passenger passenger = new Passenger();
			passenger = passengerDAO.findOne(passengerId);
			
			List<Flight> flightLists = new ArrayList<Flight>();
			
			
			int price = 0;
			
			for (String flightNumber : flightList) {
				Flight flight = new Flight();
				flight = flightDAO.findOne(flightNumber);
				flight.getPassengers().add(passenger);
				flight.setSeatsLeft(flight.getSeatsLeft()-1);
				flightLists.add(flight);
				price+=flightDAO.findOne(flightNumber).getPrice();
			}	
			reservation = new Reservation(passenger, price, flightLists);
			
			reservation = reservationDAO.save(reservation);
			//System.out.println("Order No :" + reservation.getOrderNumber());
			reservation = reservationDAO.findOne(reservation.getOrderNumber());
						
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return reservation;
		
	}
}
