package edu.sjsu.cmpe275.lab2.controllers;

import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
import edu.sjsu.cmpe275.lab2.model.ReservationForGetPassengers;

@RestController
public class PassengerController<E> {
	
	@Autowired
	private PassengerDAO passengerDAO; 
	
	@Autowired
	private ReservationDAO reservationDAO;
	
	@Autowired
	private FlightDAO flightDAO; 
	
	public ResponseEntity<E> redirectTo(URI location){
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(location);
		return (ResponseEntity<E>) new ResponseEntity<Void>(headers, HttpStatus.MOVED_PERMANENTLY);
	}
	
	@RequestMapping(value="/passenger", method=RequestMethod.POST)
    public ResponseEntity<E> createPassenger(@RequestParam(value="firstname", defaultValue="firstname") String firstname, @RequestParam(value="lastname", defaultValue="lastname") String lastname, @RequestParam(value="age", defaultValue="0") int age, @RequestParam(value="gender", defaultValue="gender") String gender, @RequestParam(value="phone", defaultValue="phone") String phone, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Passenger passenger = null;
		
		//check if unique phone number
		List<Passenger> listOfPassengers = passengerDAO.findByPhone(phone);
		
		if(listOfPassengers!=null && !listOfPassengers.isEmpty()){
			
			URI location = ServletUriComponentsBuilder
		            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "400").queryParam("msg", "another passenger with the same number already exists.").build().toUri();

			return redirectTo(location);
			
			//return new ModelAndView("/applicationError?" + "code=" + "400" + "&msg=" + "another passenger with the same number already exists.");
			
		}else{
			passenger = new Passenger(firstname,lastname, age, gender, phone);
			passengerDAO.save(passenger);
		}
		
		
		//return new ModelAndView("redirect:/passenger/" + passenger.getId() + "?json=true", model);
		URI location = ServletUriComponentsBuilder
	            .fromCurrentServletMapping().path("/passenger/" + passenger.getId()).queryParam("json", true).build().toUri();

		return redirectTo(location);
	}
	
	@RequestMapping(value="/passenger/{id}", method=RequestMethod.GET)
    public ResponseEntity<?> getPassengerWithJsonReq(@PathVariable("id") String id, @RequestParam(value="xml", defaultValue="false") boolean isXmlReq, HttpServletResponse response) throws Exception {
		
		Passenger passengerTemp = null;
		
		//check if unique phone number
		List<Passenger> listOfPassengers = passengerDAO.findById(id);
		
		if(listOfPassengers==null || listOfPassengers.isEmpty()){
			
			URI location = ServletUriComponentsBuilder
		            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "404").queryParam("msg", "Sorry, the requested passenger with id " + id + " does not exist").build().toUri();

			return redirectTo(location);
		}else{
			passengerTemp = listOfPassengers.get(0);
			
			JSONObject returnJsonVar = new JSONObject();
			
			//get passenger details
			JSONObject passenger = new JSONObject();
			passenger.put("id", passengerTemp.getId());
			passenger.put("firstname", passengerTemp.getFirstname());
			passenger.put("lastname", passengerTemp.getLastname());
			passenger.put("age", passengerTemp.getAge());
			passenger.put("gender", passengerTemp.getGender());
			passenger.put("phone", passengerTemp.getPhone());
			
			//get reservation details
			List<Reservation> listOfReservation = reservationDAO.findByPassenger(passengerTemp);
			List<ReservationForGetPassengers> listOfReservationForGetPassengers = new ArrayList<ReservationForGetPassengers>();
			
			JSONObject reservation = new JSONObject();
			
			for(Reservation currReservation: listOfReservation){
				listOfReservationForGetPassengers.add(new ReservationForGetPassengers(currReservation));
				
			}

			reservation.put("reservation", listOfReservationForGetPassengers);
			passenger.put("reservations", reservation);
			
			returnJsonVar.put("passenger", passenger);
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(returnJsonVar.toString());
			String prettyJsonString = gson.toJson(je);
			
			if(!isXmlReq){
				return new ResponseEntity(prettyJsonString,HttpStatus.OK);
			}else{
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
			
		}
	}
	
	@RequestMapping(value="/passenger/{id}", method=RequestMethod.PUT)
    public ResponseEntity<E> updatePassenger(@PathVariable("id") String id, @RequestParam(value="firstname", defaultValue="firstname") String firstname, @RequestParam(value="lastname", defaultValue="lastname") String lastname, @RequestParam(value="age", defaultValue="0") int age, @RequestParam(value="gender", defaultValue="gender") String gender, @RequestParam(value="phone", defaultValue="phone") String phone, ModelMap model, HttpServletResponse response) throws Exception {
		Passenger passenger = null;
		
		//check if unique phone number
		List<Passenger> listOfPassengersWithTheId = passengerDAO.findById(id);
		List<Passenger> listOfPassengersWithThePhone = passengerDAO.findByPhone(phone);
		
		if(listOfPassengersWithTheId==null || listOfPassengersWithTheId.isEmpty()){
			URI location = ServletUriComponentsBuilder
		            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "404").queryParam("msg", "Sorry, the requested passenger with id " + id + " does not exist").build().toUri();

			return redirectTo(location);
			
		}else if (listOfPassengersWithThePhone!=null && !listOfPassengersWithThePhone.isEmpty() && !id.equalsIgnoreCase(listOfPassengersWithTheId.get(0).getId())){
			URI location = ServletUriComponentsBuilder
		            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "400").queryParam("msg", "Another passenger with the same number already exists.").build().toUri();

			return redirectTo(location);
		}else{
			passenger = listOfPassengersWithTheId.get(0);
			passenger.setAge(age);
			passenger.setFirstname(firstname);
			passenger.setGender(gender);
			passenger.setId(id);
			passenger.setLastname(lastname); 
			passenger.setPhone(phone);
			
			passengerDAO.save(passenger);
		}
		//return new ModelAndView("forward:/passenger/" + passenger.getId() + "?json=true", model);
		URI location = ServletUriComponentsBuilder
	            .fromCurrentServletMapping().path("/passenger/" + passenger.getId()).queryParam("json", true).build().toUri();

		return redirectTo(location);
	}
	
	@RequestMapping(value="/passenger/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<E> deletePassenger(@PathVariable("id") String id) throws Exception {
		Passenger passenger = null;
		
		//check if unique phone number
		List<Passenger> listOfPassengers = passengerDAO.findById(id);
		
		if(listOfPassengers==null || listOfPassengers.isEmpty()){
			URI location = ServletUriComponentsBuilder
		            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "404").queryParam("msg", "Passenger with id " + id + " does not exist").build().toUri();

			return redirectTo(location);
			
		}else{
			passenger = listOfPassengers.get(0);
			
			//fetch reservations for this passenger
			List<Reservation> reservationList = reservationDAO.findByPassenger(passenger);
			for(Reservation reservation : reservationList){
				//fetch every flight in this reservation
				for(Flight flight:reservation.getFlights()){
					int noOfPassengers = flight.getSeatsLeft();
					if(noOfPassengers > 0){
						noOfPassengers--;
						flight.setSeatsLeft(noOfPassengers);
					}
				}
				reservationDAO.delete(reservation);
			}
			
			passengerDAO.delete(passenger);
		}
		//return new ModelAndView("forward:/passenger/" + passenger.getId() + "?json=true", model);
		URI location = ServletUriComponentsBuilder
	            .fromCurrentServletMapping().path("/applicationErrorInXML").queryParam("code", "200").queryParam("msg", "Passenger with id " + id + " is deleted successfully").build().toUri();

		return redirectTo(location);
	}
	
}
