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
	Reservation reservation = null;

	@RequestMapping(value="/reservation",method=RequestMethod.POST)
    public @ResponseBody Reservation createReservation(@RequestParam(value="passengerId") String passengerId, @RequestParam(value="flightLists") String[] flightList) {
		try {
			Passenger passenger = new Passenger();
			passenger.setId(passengerId);
			List<Flight> flightLists = new ArrayList<Flight>();
			
			
			int price = 0;
			System.out.println(flightList);
			for (String flightNumber : flightList) {
				Flight flight = new Flight();
				System.out.println(flightNumber);
				flight.setNumber(flightNumber);
				flightLists.add(flight);
				price+=flightDAO.findOne(flightNumber).getPrice();
			}	
			
			//int price = flightDAO.findPrice(flightList);
			System.out.println(price);
			System.out.println(flightLists.toString());
			reservation = new Reservation(passenger, price, flightLists);
			System.out.println(reservationDAO.save(reservation).getPassenger());
			
			/*passenger = new Passenger(firstname,lastname, age, gender, phone);
			passengerDAO.save(passenger);*/
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return reservation;
		
	}
}
