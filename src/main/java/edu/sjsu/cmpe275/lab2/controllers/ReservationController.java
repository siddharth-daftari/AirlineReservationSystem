package edu.sjsu.cmpe275.lab2.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import edu.sjsu.cmpe275.lab2.dao.FlightDAO;
import edu.sjsu.cmpe275.lab2.dao.PassengerDAO;
import edu.sjsu.cmpe275.lab2.dao.ReservationDAO;
import edu.sjsu.cmpe275.lab2.model.Flight;
import edu.sjsu.cmpe275.lab2.model.Passenger;
import edu.sjsu.cmpe275.lab2.model.Reservation;

@RestController
public class ReservationController<E> {
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
	
	//https://hostname/reservation/number
	@RequestMapping(value="/reservation/{number}", method=RequestMethod.GET)
	public ResponseEntity<E> getReservationJSON(@PathVariable("number") String orderNumber,HttpServletResponse response) throws Exception{
		
		Reservation reservation = reservationDAO.findOne(orderNumber);
		
		JSONObject reservationObj = new JSONObject();
		
		reservationObj.put("orderNumber",reservation.getOrderNumber());
		reservationObj.put("price",reservation.getPrice());
		
		JSONObject passenger = new JSONObject();
		passenger.put("id",reservation.getPassenger().getId());
		passenger.put("firstname",reservation.getPassenger().getFirstname());
		passenger.put("lastname",reservation.getPassenger().getLastname());
		passenger.put("age",reservation.getPassenger().getAge());
		passenger.put("gender",reservation.getPassenger().getGender());
		passenger.put("phone",reservation.getPassenger().getPhone());
		
		reservationObj.put("passenger",passenger);
		
		JSONObject listOfFlights = new JSONObject();
		listOfFlights.put("flight", reservation.getFlights());
		
		//need to remove the passengers list from flight
		reservationObj.put("flights", listOfFlights);
						
		JSONObject returnJsonVar = new JSONObject();
		returnJsonVar.put("reservation",reservationObj);
		
		return convertToJSON(returnJsonVar);
	}
	
	public ResponseEntity convertToJSON(JSONObject returnJsonVar){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(returnJsonVar.toString());
		String prettyJsonString = gson.toJson(je);
		
		return new ResponseEntity(prettyJsonString,HttpStatus.OK);
	}
	
}
