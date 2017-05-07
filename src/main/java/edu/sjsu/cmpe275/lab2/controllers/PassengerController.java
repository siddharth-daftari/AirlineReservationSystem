package edu.sjsu.cmpe275.lab2.controllers;

import java.io.StringWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

import edu.sjsu.cmpe275.lab2.dao.PassengerDAO;
import edu.sjsu.cmpe275.lab2.dao.ReservationDAO;
import edu.sjsu.cmpe275.lab2.model.CustomException;
import edu.sjsu.cmpe275.lab2.model.Flight;
import edu.sjsu.cmpe275.lab2.model.Passenger;
import edu.sjsu.cmpe275.lab2.model.Plane;
import edu.sjsu.cmpe275.lab2.model.Reservation;


/**
 * @author siddharth and parvez
 *
 * @param <E>
 */
@RestController
public class PassengerController<E> {
	
	@Autowired
	private PassengerDAO passengerDAO; 
	
	@Autowired
	private ReservationDAO reservationDAO;
	
	/**
	 * Description: method to redirect to the given location
	 * @param location
	 * @return
	 */
	public ResponseEntity<E> redirectTo(URI location){
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(location);
		return (ResponseEntity<E>) new ResponseEntity<Void>(headers, HttpStatus.MOVED_PERMANENTLY);
	}
	
	/**
	 * Description: method to create a passenger
	 * @param firstname
	 * @param lastname
	 * @param age
	 * @param gender
	 * @param phone
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@Transactional
	@RequestMapping(value="/passenger", method=RequestMethod.POST)
    public ResponseEntity<E> createPassenger(@RequestParam(value="firstname", defaultValue="firstname") String firstname, @RequestParam(value="lastname", defaultValue="lastname") String lastname, @RequestParam(value="age", defaultValue="0") int age, @RequestParam(value="gender", defaultValue="gender") String gender, @RequestParam(value="phone", defaultValue="phone") String phone, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Passenger passenger = null;
		
		//check if unique phone number
		List<Passenger> listOfPassengers = passengerDAO.findByPhone(phone);
		
		if(listOfPassengers!=null && !listOfPassengers.isEmpty()){
			
			throw new CustomException("400", "another passenger with the same number already exists.");
			
		}else{
			passenger = new Passenger(firstname,lastname, age, gender, phone);
			passengerDAO.save(passenger);
		}
		
		URI location = ServletUriComponentsBuilder
	            .fromCurrentServletMapping().path("/passenger/" + passenger.getId()).queryParam("json", true).build().toUri();

		return redirectTo(location);
	}
	
	/**
	 * Description: method to get the details of a passenger 
	 * @param id
	 * @param isXmlReq
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@Transactional
	@RequestMapping(value="/passenger/{id}", method=RequestMethod.GET)
    public ResponseEntity<?> getPassengerWithJsonReq(@PathVariable("id") String id, @RequestParam(value="xml", defaultValue="false") boolean isXmlReq, HttpServletResponse response) throws Exception {
		
		Passenger passengerTemp = null;
		
		//check if unique phone number
		List<Passenger> listOfPassengers = passengerDAO.findById(id);
		
		if(listOfPassengers==null || listOfPassengers.isEmpty()){
			
			throw new CustomException("404", "Sorry, the requested passenger with id " + id + " does not exist");
			
		}else{
			passengerTemp = listOfPassengers.get(0);
			List<Reservation> listOfReservation = reservationDAO.findByPassenger(passengerTemp);
			
			JSONObject returnJsonVar = new JSONObject();
			
			//get passenger details
			JSONObject passenger = new JSONObject();
			JSONObject reservationJSONVar = new JSONObject();
			List<JSONObject> reservationListJSONVar = new ArrayList<JSONObject>();
			
			passenger.put("id", passengerTemp.getId());
			passenger.put("firstname", passengerTemp.getFirstname());
			passenger.put("lastname", passengerTemp.getLastname());
			passenger.put("age", Integer.toString(passengerTemp.getAge()));
			passenger.put("gender", passengerTemp.getGender());
			passenger.put("phone", passengerTemp.getPhone());
			
			for(Reservation currReservation: listOfReservation){
				JSONObject reservationTempJSON = new JSONObject();
				List<Flight> listOfFlight = currReservation.getFlights();
				
				reservationTempJSON.put("orderNumber",currReservation.getOrderNumber());
				reservationTempJSON.put("price", Integer.toString(currReservation.getPrice()));
				
				List<JSONObject> flightListJSONVar = new ArrayList<JSONObject>();
				for(Flight currFlight: listOfFlight){
					JSONObject flightTempJSON = new JSONObject();
					flightTempJSON.put("number", currFlight.getNumber());
					flightTempJSON.put("price", Integer.toString(currFlight.getPrice()));
					flightTempJSON.put("from", currFlight.getFrom());
					flightTempJSON.put("to", currFlight.getTo());
					flightTempJSON.put("departureTime", new SimpleDateFormat("yyyy-MM-dd-hh").format(currFlight.getDepartureTime()));
					flightTempJSON.put("arrivalTime", new SimpleDateFormat("yyyy-MM-dd-hh").format(currFlight.getArrivalTime()));
					flightTempJSON.put("description", currFlight.getDescription());
					
					JSONObject planeJSONVar = new JSONObject();
					Plane plane = currFlight.getPlane();
					
					planeJSONVar.put("capacity", plane.getCapacity());
					planeJSONVar.put("model", plane.getModel());
					planeJSONVar.put("manufacturer", plane.getManufacturer());
					planeJSONVar.put("yearOfManufacture", Integer.toString(plane.getYearOfManufacture()));
					
					flightTempJSON.put("plane", planeJSONVar);
					
					flightListJSONVar.add(flightTempJSON);
				}
				
				JSONObject flightTemp = new JSONObject();
				flightTemp.put("flight",flightListJSONVar);
				reservationTempJSON.put("flights",flightTemp);
				
				reservationListJSONVar.add(reservationTempJSON);
			}
			
			reservationJSONVar.put("reservation",reservationListJSONVar );
			passenger.put("reservations", reservationJSONVar);
			
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
	            
	            return (ResponseEntity<E>) new ResponseEntity<String>(stringWriter.toString(),HttpStatus.OK);
			}
			
		}
	}

	/**
	 * Description: method for updating passenger details
	 * @param id
	 * @param firstname
	 * @param lastname
	 * @param age
	 * @param gender
	 * @param phone
	 * @param model
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@Transactional
	@RequestMapping(value="/passenger/{id}", method=RequestMethod.PUT)
    public ResponseEntity<E> updatePassenger(@PathVariable("id") String id, @RequestParam(value="firstname", defaultValue="firstname") String firstname, @RequestParam(value="lastname", defaultValue="lastname") String lastname, @RequestParam(value="age", defaultValue="0") int age, @RequestParam(value="gender", defaultValue="gender") String gender, @RequestParam(value="phone", defaultValue="phone") String phone, ModelMap model, HttpServletResponse response) throws Exception {
		Passenger passenger = null;
		
		//check if unique phone number
		List<Passenger> listOfPassengersWithTheId = passengerDAO.findById(id);
		List<Passenger> listOfPassengersWithThePhone = passengerDAO.findByPhone(phone);
		
		if(listOfPassengersWithTheId==null || listOfPassengersWithTheId.isEmpty()){
			
			throw new CustomException("404", "Sorry, the requested passenger with id " + id + " does not exist");
			
		}else if (listOfPassengersWithThePhone!=null && !listOfPassengersWithThePhone.isEmpty() && !id.equalsIgnoreCase(listOfPassengersWithThePhone.get(0).getId())){
			
			throw new CustomException("400", "Another passenger with the same number already exists.");
			
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
		URI location = ServletUriComponentsBuilder
	            .fromCurrentServletMapping().path("/passenger/" + passenger.getId()).queryParam("json", true).build().toUri();

		return redirectTo(location);
	}
	
	/**
	 * Description: method to delete passenger
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@Transactional
	@RequestMapping(value="/passenger/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<E> deletePassenger(@PathVariable("id") String id) throws Exception {
		Passenger passenger = null;
		
		//check if unique phone number
		List<Passenger> listOfPassengers = passengerDAO.findById(id);
		
		if(listOfPassengers==null || listOfPassengers.isEmpty()){
			throw new CustomException("404", "Passenger with id " + id + " does not exist");
			
		}else{
			passenger = listOfPassengers.get(0);
			
			//fetch reservations for this passenger
			List<Reservation> reservationList = reservationDAO.findByPassenger(passenger);
			for(Reservation reservation : reservationList){
				//fetch every flight in this reservation
				for(Flight flight:reservation.getFlights()){
					int noOfPassengers = flight.getSeatsLeft();
					if(noOfPassengers > 0){
						noOfPassengers++;
						flight.setSeatsLeft(noOfPassengers);
						flight.getPassengers().remove(reservation.getPassenger());
						
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
	
	/**
	 * Description: Exception handler for PassengerController
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = CustomException.class)
	public ResponseEntity<E> customeExceptionHandler(CustomException e){
		
		URI location = ServletUriComponentsBuilder
	            .fromCurrentServletMapping().path("/applicationError").queryParam("code", e.getCode()).queryParam("msg", e.getMsg()).build().toUri();

		return redirectTo(location);
	}
	
}
