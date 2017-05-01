package edu.sjsu.cmpe275.lab2.controllers;
import java.util.*;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
	
	public ResponseEntity<E> redirectTo(URI location){
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(location);
		return (ResponseEntity<E>) new ResponseEntity<Void>(headers, HttpStatus.MOVED_PERMANENTLY);
	}
	
	@RequestMapping(value="/reservation",method=RequestMethod.POST)
    public ResponseEntity<E> createReservation(@RequestParam(value="passengerId") String passengerId, @RequestParam(value="flightLists") String[] flightList) throws Exception, IOException {
		Reservation reservation = null;
		try {
			Passenger passenger = new Passenger();
			passenger = passengerDAO.findOne(passengerId);
			URI location;
			List<Flight> flightLists = new ArrayList<Flight>();
			
			
			int price = 0;
			
				
			
			//check for the time conflict
			int flag=0;
			for(int i=0; i<flightList.length;i++){
				Flight fl1 = new Flight();
				fl1 = flightDAO.findOne(flightList[i]);
				Date fl1ArrivalTime = fl1.getArrivalTime();
				Date fl1DeptTime = fl1.getDepartureTime();
				for(int j=i+1;j<flightList.length;j++){
					Flight fl2 = new Flight();
					fl2 = flightDAO.findOne(flightList[j]);
					Date fl2ArrivalTime = fl2.getArrivalTime();
					Date fl2DeptTime = fl2.getDepartureTime();
					
					if(((fl1DeptTime.compareTo(fl2ArrivalTime) >= 0) &&(fl1ArrivalTime.compareTo(fl2ArrivalTime)<=0) ) ||((fl1ArrivalTime.compareTo(fl2DeptTime)<=0) && (fl2DeptTime.compareTo(fl1DeptTime)<=0)) ){
						flag = 1;
						break;
					}
				}
				if(flag==1){
					System.out.println(" Flight can't be added ");
					
					location = ServletUriComponentsBuilder
				            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "404").queryParam("msg", "Sorry, There is a time overlap in the flights").build().toUri();
					return redirectTo(location);
				}
			}
			
			
			
			/*
			 * if there is no time conflict create the reservation.
			*/
			for (String flightNumber : flightList) {
				Flight flight = new Flight();
				flight = flightDAO.findOne(flightNumber);
				if(flight.getSeatsLeft()==0){
					location = ServletUriComponentsBuilder
				            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "404").queryParam("msg", "Sorry, flight "+flightNumber+" is completely booked!").build().toUri();
					return redirectTo(location);
				}
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
		
		return convertToXml(generateReservationJSONObject(reservation));
		
	}
	
	//https://hostname/reservation/number
	@RequestMapping(value="/reservation/{number}", method=RequestMethod.GET)
	public ResponseEntity<E> getReservationJSON(@PathVariable("number") String orderNumber,HttpServletResponse response) throws Exception{
		
		Reservation reservation = reservationDAO.findOne(orderNumber);
		
		return convertToJSON(generateReservationJSONObject(reservation));
	}
	
	@RequestMapping(value="/reservation/{order_number}",method=RequestMethod.DELETE)
	public ResponseEntity<E> cancelReservation(@PathVariable(value = "order_number")String orderNumber){
		//find the reservation
		
		Reservation reservation = new Reservation();
		reservation = reservationDAO.findOne(orderNumber);
		URI location = null;
		if(reservation==null){
			 location = ServletUriComponentsBuilder
		            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "404").queryParam("msg", "Sorry, the requested reservation with order number " + orderNumber + " does not exist").build().toUri();
		
		}
		else{
			for(Flight flight : reservation.getFlights()){
				flight.setSeatsLeft(flight.getSeatsLeft()+1);
				flight.getPassengers().remove(reservation.getPassenger());
			}
			
			
			reservationDAO.delete(reservation);
			//passengerDAO.delete(reservation.getPassenger());
			 location = ServletUriComponentsBuilder
		            .fromCurrentServletMapping().path("/applicationErrorInXML").queryParam("code", "200").queryParam("msg", "Reservation order number " + orderNumber + " is cancelled successfully").build().toUri();

		}
		
		
		return redirectTo(location);
	}
	
	//https://hostname/reservation/number?flightsAdded=AA,BB,CC&flightsRemoved=XX,YY
	@RequestMapping(value="/reservation/{order_number}",method=RequestMethod.POST)
	public ResponseEntity<E> updateReservation(@PathVariable(value="order_number")String orderNumber,@RequestParam(value="flightsAdded", required=false) String[] flightsAdded,@RequestParam(value="flightsRemoved",required=false) String[] flightsRemoved) {
		
		Reservation reservation = reservationDAO.findOne(orderNumber);
		Passenger passenger = passengerDAO.findOne(reservation.getPassenger().getId());
		int reservationPrice = reservation.getPrice();
		URI location;
		//TODO: check for time conflicts
		
		if(flightsRemoved!=null){
			//TODO: check for empty list
			if(flightsRemoved.length==0){
				location = ServletUriComponentsBuilder
			            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "404").queryParam("msg", "Sorry, flightsRemoved list cannot be empty").build().toUri();
				return redirectTo(location);
			}
			//System.out.println(flightsRemoved[0]);
			for (String flightNumber : flightsRemoved) {
				Flight removedflight = new Flight();
				removedflight = flightDAO.findOne(flightNumber);
				
				//update the reservation price by adding the new flight price
				reservationPrice= reservationPrice - removedflight.getPrice();
				//update the seats left
				removedflight.setSeatsLeft(removedflight.getSeatsLeft()+1);
				//remove the passenger
				removedflight.getPassengers().remove(reservation.getPassenger());
				//remove the flight from the reservation
				reservation.getFlights().remove(removedflight);
			}	
			reservation.setPrice(reservationPrice);
		}
		
			
		if(flightsAdded!=null){
			if(flightsAdded.length==0){
				location = ServletUriComponentsBuilder
			            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "404").queryParam("msg", "Sorry, flightsAdded list cannot be empty.").build().toUri();
				return redirectTo(location);
			}
			System.out.println("Flight added are there");
			for (String flightNumber : flightsAdded) {
				System.out.println("Flight "+flightNumber);
				Flight addedflight = new Flight();
				addedflight = flightDAO.findOne(flightNumber);
				//check if plane capacity exceeds or not
				if(addedflight.getSeatsLeft()==0){
					location = ServletUriComponentsBuilder
				            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "404").queryParam("msg", "Sorry, flight "+flightNumber+" is completely booked!").build().toUri();
					return redirectTo(location);
				}
				
				
				Date arrivalTime = addedflight.getArrivalTime();
				Date departureTime = addedflight.getDepartureTime();
				int flag =0;
				String conflictedFlightNumber="";
				for(Flight bookedFlights : reservation.getFlights()){
					
					Date bookedArrivalTime = bookedFlights.getArrivalTime();
					Date bookedDepartureTime = bookedFlights.getDepartureTime();
					
					if(((bookedDepartureTime.compareTo(arrivalTime) >= 0) &&(bookedArrivalTime.compareTo(arrivalTime)<=0) ) ||((bookedArrivalTime.compareTo(departureTime)<=0) && (departureTime.compareTo(bookedDepartureTime)<=0)) ){
						flag = 1;
						conflictedFlightNumber =bookedFlights.getNumber();
						break;
					}
				}
				System.out.println(flag);
				if(flag == 1){
					//Time overlap. Send 404
					System.out.println(" Flight can't be added ");
					
					location = ServletUriComponentsBuilder
				            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "404").queryParam("msg", "Sorry, There is a time overlap between flight " + flightNumber + " and flight "+conflictedFlightNumber).build().toUri();
					return redirectTo(location);
					
				} else{
					System.out.println("Adding passenger to flight");
					//add the passenger to the flight
					addedflight.getPassengers().add(passenger);
					//update the seats
					System.out.println("Updating the seats flight");
					
					addedflight.setSeatsLeft(addedflight.getSeatsLeft()-1);
					//update the reservation price by adding the new flight price
					reservationPrice= reservationPrice + addedflight.getPrice();
					//add the flight to the reservation
					System.out.println("Updating the flights");
					reservation.getFlights().add(addedflight);				
				}
			}	
			reservation.setPrice(reservationPrice);
		}
		reservationDAO.save(reservation);
		return convertToJSON(generateReservationJSONObject(reservation));
		
		
	}
	
	
	/*Work in progress*/
	public ResponseEntity<E> searchReservation(@RequestParam(value="passengerId",required=false) String passengerId, @RequestParam(value="from",required=false) String from, @RequestParam(value="to",required=false) String to,@RequestParam(value="flightNumber") String flightNumber){
		
		Reservation reservation = new Reservation();
		ReservationDAO reservationDAO;
		
		return null;
	}
	
	public ResponseEntity convertToJSON(JSONObject returnJsonVar){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(returnJsonVar.toString());
		String prettyJsonString = gson.toJson(je);
		
		return new ResponseEntity(prettyJsonString,HttpStatus.OK);
	}
	
	public ResponseEntity<E> convertToXml(JSONObject returnJsonVar) throws DocumentException, IOException{
		
		String xml = XML.toString(returnJsonVar);
		
		//converting XML to pretty print format
		Document document = DocumentHelper.parseText(xml);  
        StringWriter stringWriter = new StringWriter();  
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();  
        outputFormat.setIndent(true);
        outputFormat.setIndentSize(3); 
        outputFormat.setSuppressDeclaration(true);
        outputFormat.setNewLineAfterDeclaration(false);
        XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);  
        xmlWriter.write(document);  
        
        //return stringWriter.toString();
        return (ResponseEntity<E>) new ResponseEntity<String>(stringWriter.toString(),HttpStatus.OK);
	}
	
	
	public JSONObject generateReservationJSONObject(Reservation reservation){
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
		
		return returnJsonVar;
	}
}
