package edu.sjsu.cmpe275.lab2.controllers;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.cmpe275.lab2.dao.FlightDAO;
import edu.sjsu.cmpe275.lab2.model.Flight;
import edu.sjsu.cmpe275.lab2.model.Plane;

@RestController
public class FlightController {
	
	@Autowired
	private FlightDAO flightDAO;
	//https://hostname/flight/flightNumber?price=120&from=AA&to=BB&departureTime=CC&arrivalTime=DD&description=EE&capacity=GG&model=HH&manufacturer=II&yearOfManufacture=1997
	@RequestMapping(value="/flight/{flight_number}",method = RequestMethod.POST)
	public Flight createOrUpdateFlight(@PathVariable(value = "flight_number") String flightNumber, @RequestParam(value="price") int price, @RequestParam(value="from") String from, @RequestParam(value="to") String to, @RequestParam(value="departureTime") String departureTime, @RequestParam(value="arrivalTime") String arrivalTime,@RequestParam(value="description") String description,@RequestParam(value="capacity") int capacity,@RequestParam(value="model") String model,@RequestParam(value="manufacturer") String manufacturer,@RequestParam(value="yearOfManufacture") int yearOfManufacture) throws ParseException{
		
		Plane plane = new Plane(capacity,model,manufacturer,yearOfManufacture);
		Flight flight = new Flight(flightNumber, price, from, to, (Date) new SimpleDateFormat("yyyy-MM-dd-hh").parse(departureTime), (Date) new SimpleDateFormat("yyyy-MM-dd-hh").parse(arrivalTime), capacity, description, plane, null);
		
		try{
			flightDAO.save(flight);
			return flight;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
