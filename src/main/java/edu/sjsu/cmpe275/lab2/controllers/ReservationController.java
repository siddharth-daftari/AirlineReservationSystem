package edu.sjsu.cmpe275.lab2.controllers;
import java.util.*;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.text.SimpleDateFormat;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
import edu.sjsu.cmpe275.lab2.model.Flight_;
import edu.sjsu.cmpe275.lab2.model.Passenger;
import edu.sjsu.cmpe275.lab2.model.Passenger_;
import edu.sjsu.cmpe275.lab2.model.Plane;
import edu.sjsu.cmpe275.lab2.model.Reservation;
import edu.sjsu.cmpe275.lab2.model.Reservation_;

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
		URI location;
		try {
			Passenger passenger = new Passenger();
			passenger = passengerDAO.findOne(passengerId);
			
			List<Flight> flightLists = new ArrayList<Flight>();
			
			//fetch all the past flights of this passenger
			Specification spec = new Specification<Flight>() {
			    
				@Override
				public Predicate toPredicate(Root<Flight> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
					
					List<Predicate> predicates = new ArrayList<>();
					
					Join<Flight,Passenger> joinRoot = root.join(Flight_.passengers);
					
				    predicates.add(cb.equal(joinRoot.get(Passenger_.id), passengerId ));
					
				    cq.distinct(true);
				    return andTogether(predicates, cb);
				}
				
				private Predicate andTogether(List<Predicate> predicates, CriteriaBuilder cb) {
					
				    return cb.and(predicates.toArray(new Predicate[0]));
				}
			};
			
			List<Flight> pastFlightLists = flightDAO.findAll(spec);
			
			if(pastFlightLists != null && !pastFlightLists.isEmpty()){
				
			}else{
				pastFlightLists = new ArrayList<Flight>();
			}
			
			//adding current flights (provided in the URL) to this list
			for(String flightNumber : flightList){
				Flight flightTemp = flightDAO.findByNumber(flightNumber);
				if(flightTemp!=null)
					pastFlightLists.add(flightTemp);
			}
			//
			int price = 0;
			
			int pastFlightListsSize = pastFlightLists.size();
			//for each flight checking overlap and seats left
			for (int i=0; i<pastFlightListsSize; i++) {
				Flight flight = pastFlightLists.get(0); 
				//check for time overlap
				pastFlightLists.remove(flight);
				Date newArrivalTime = flight.getArrivalTime();
				Date newDepartureTime = flight.getDepartureTime();
				
				for(Flight flightTemp : pastFlightLists){
					
					if(!(flight.equals(flightTemp)) && (newArrivalTime.before(flightTemp.getDepartureTime()) || newDepartureTime.after(flightTemp.getArrivalTime()))){
					
					}else{
						location = ServletUriComponentsBuilder
					            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "400").queryParam("msg", "Sorry, there is a time overlap in the flights.").build().toUri();

						return redirectTo(location);
					}
					
				}
				//check for seats left
				if(flight.getSeatsLeft()==0){
					location = ServletUriComponentsBuilder
				            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "404").queryParam("msg", "Sorry, flight "+ flight.getNumber() +" is completely booked!").build().toUri();
					return redirectTo(location);
				}
				
				//both criteria satisfied
				flight.getPassengers().add(passenger);
				flight.setSeatsLeft(flight.getSeatsLeft()-1);
				flightLists.add(flight);
				price+=flightDAO.findOne(flight.getNumber()).getPrice();
			}	
			
			reservation = new Reservation(passenger, price, flightLists);
			
			reservation = reservationDAO.save(reservation);
			reservation = reservationDAO.findOne(reservation.getOrderNumber());
						
		}catch(Exception e){
			e.printStackTrace();
			location = ServletUriComponentsBuilder
		            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "500").queryParam("msg", "Sorry, something went wrong.").build().toUri();
			return redirectTo(location);
		}
		
		location = ServletUriComponentsBuilder
	            .fromCurrentServletMapping().path("/reservation/" + reservation.getOrderNumber()).queryParam("xml", true).build().toUri();

		return redirectTo(location);
	}
	
	//https://hostname/reservation/number
	@RequestMapping(value="/reservation/{number}", method=RequestMethod.GET)
	public ResponseEntity<E> getReservationJSON(@PathVariable("number") String orderNumber, @RequestParam(value="xml", defaultValue="false") boolean isXmlReq, HttpServletResponse response) throws Exception{
		
		Reservation reservation = reservationDAO.findOne(orderNumber);
		
		if(reservation==null){
			
			URI location = ServletUriComponentsBuilder
		            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "404").queryParam("msg", "Reserveration with number " + orderNumber + " does not exist ").build().toUri();

			return redirectTo(location);
		}else{
			
			JSONObject returnJsonVar = new JSONObject();
			JSONObject reservationTempJSON = new JSONObject();
			
			List<Flight> listOfFlight = reservation.getFlights();
			
			reservationTempJSON.put("orderNumber",reservation.getOrderNumber());
			reservationTempJSON.put("price", Integer.toString(reservation.getPrice()));
			
			JSONObject passengerJSON = new JSONObject();
			Passenger passenger = reservation.getPassenger();
			passengerJSON.put("id", passenger.getId());
			passengerJSON.put("firstname", passenger.getFirstname());
			passengerJSON.put("lastname", passenger.getLastname());
			passengerJSON.put("age", Integer.toString(passenger.getAge()));
			passengerJSON.put("gender", passenger.getGender());
			passengerJSON.put("phone", passenger.getPhone());
			
			reservationTempJSON.put("passenger", passengerJSON);
			
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
				flightTempJSON.put("seatsLeft", Integer.toString(currFlight.getSeatsLeft()));
				
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
			returnJsonVar.put("reservation",reservationTempJSON );
			
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
	
	@RequestMapping(value="/reservation/{order_number}",method=RequestMethod.DELETE)
	public ResponseEntity<E> cancelReservation(@PathVariable(value = "order_number")String orderNumber){
		//find the reservation
		
		Reservation reservation = new Reservation();
		reservation = reservationDAO.findOne(orderNumber);
		URI location = null;
		if(reservation==null){
			 location = ServletUriComponentsBuilder
		            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "404").queryParam("msg", "Reservation with number " + orderNumber + " does not exist ").build().toUri();
		
		}
		else{
			for(Flight flight : reservation.getFlights()){
				flight.setSeatsLeft(flight.getSeatsLeft()+1);
				flight.getPassengers().remove(reservation.getPassenger());
				flightDAO.save(flight);
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
	
	
	@RequestMapping(value="/reservation",method=RequestMethod.GET)
	public ResponseEntity<E> searchReservation(@RequestParam(value="passengerId",defaultValue="") String passengerId, @RequestParam(value="from",defaultValue="") String from, @RequestParam(value="to",defaultValue="") String to,@RequestParam(value="flightNumber",defaultValue="") String flightNumber) throws DocumentException, IOException{
		
		Specification spec = new Specification<Reservation>() {
		    
			@Override
			public Predicate toPredicate(Root<Reservation> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
				
				List<Predicate> predicates = new ArrayList<>();
				
				Join<Reservation,Flight> joinRoot = root.join(Reservation_.flights);
				
			    if (!"".equalsIgnoreCase(passengerId)) {
			      //predicates.add(cb.like(root.get(Reservation_.passenger).get(Passenger_.id), passengerId + "%"));
			    	predicates.add(cb.equal(root.get(Reservation_.passenger).get(Passenger_.id), passengerId ));
			    }
			    
			    if (!"".equalsIgnoreCase(from)) {
			    	
			    	predicates.add(cb.equal(joinRoot.get(Flight_.from), from));
				}
			    
				if (!"".equalsIgnoreCase(to)) {
							    	
			    	predicates.add(cb.equal(joinRoot.get(Flight_.to), to));
				}
				
				if (!"".equalsIgnoreCase(flightNumber)) {
			    	
			    	predicates.add(cb.equal(joinRoot.get(Flight_.number), flightNumber ));
				}
				cq.distinct(true);
			    return andTogether(predicates, cb);
			}
			
			private Predicate andTogether(List<Predicate> predicates, CriteriaBuilder cb) {
				
			    return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		
		//reservationList contains the result
		List<Reservation> reservationList = reservationDAO.findAll(spec);
		
		JSONObject returnJsonVar = new JSONObject();
		JSONObject reservationJSONVar = new JSONObject();
		List<JSONObject> reservationListJSONVar = new ArrayList<JSONObject>();
		
		for(Reservation r : reservationList){
			System.out.println(r.getOrderNumber());
		}
		
		for(Reservation currReservation: reservationList){
			JSONObject reservationTempJSON = new JSONObject();
			List<Flight> listOfFlight = currReservation.getFlights();
			
			reservationTempJSON.put("orderNumber",currReservation.getOrderNumber());
			reservationTempJSON.put("price", Integer.toString(currReservation.getPrice()));
			
			JSONObject passengerJSON = new JSONObject();
			Passenger passenger = currReservation.getPassenger();
			passengerJSON.put("id", passenger.getId());
			passengerJSON.put("firstname", passenger.getFirstname());
			passengerJSON.put("lastname", passenger.getLastname());
			passengerJSON.put("age", Integer.toString(passenger.getAge()));
			passengerJSON.put("gender", passenger.getGender());
			passengerJSON.put("phone", passenger.getPhone());
			
			reservationTempJSON.put("passenger", passengerJSON);
			
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
		returnJsonVar.put("reservations", reservationJSONVar);
		return convertToXml(returnJsonVar);
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
		passenger.put("age",Integer.toString(reservation.getPassenger().getAge()));
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
