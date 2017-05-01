package edu.sjsu.cmpe275.lab2.controllers;


import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import edu.sjsu.cmpe275.lab2.dao.FlightDAO;
import edu.sjsu.cmpe275.lab2.model.Flight;
import edu.sjsu.cmpe275.lab2.model.Plane;
import edu.sjsu.cmpe275.lab2.model.Reservation;

@RestController
public class FlightController<E> {
	
	@Autowired
	private FlightDAO flightDAO;
	
	public ResponseEntity<E> redirectTo(URI location){
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(location);
		return (ResponseEntity<E>) new ResponseEntity<Void>(headers, HttpStatus.MOVED_PERMANENTLY);
	}
	//https://hostname/flight/flightNumber?xml=true
	@RequestMapping(value="/flightXML/{flight_number}", method=RequestMethod.GET)
    public ResponseEntity<E> getFlightWithXmlReq(@PathVariable("flight_number") String flightNumber,HttpServletResponse response) throws Exception {
		
		Flight flight = flightDAO.findOne(flightNumber);
		
		if(flight==null){
			URI location = ServletUriComponentsBuilder
		            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "404").queryParam("msg", "Sorry, the requested flight with flight number " + flightNumber + " does not exist").build().toUri();

			return redirectTo(location);
		}
		else{
			JSONObject flightObj = new JSONObject();
			flightObj.put("flightNumber", flight.getNumber());
			flightObj.put("price", flight.getPrice());
			flightObj.put("from", flight.getFrom());
			flightObj.put("to", flight.getTo());
			flightObj.put("departureTime", flight.getDepartureTime());
			flightObj.put("arrivalTime", flight.getArrivalTime());
			flightObj.put("description", flight.getDescription());
			flightObj.put("seatsLeft", flight.getSeatsLeft());
			
			JSONObject planeObj = new JSONObject();
			planeObj.put("capacity",flight.getPlane().getCapacity());
			planeObj.put("model",flight.getPlane().getModel());
			planeObj.put("manufacturer",flight.getPlane().getManufacturer());
			planeObj.put("yearOfManufacture",flight.getPlane().getYearOfManufacture());
			flightObj.put("plane", planeObj);
			
			JSONObject listOfPassengers = new JSONObject();
			listOfPassengers.put("passenger", flight.getPassengers());
			System.out.println(flight.getPassengers().size());
		
			flightObj.put("passengers", listOfPassengers);
			JSONObject returnJsonVar = new JSONObject();
			returnJsonVar.put("flight", flightObj);
				
			
			return convertToXml(returnJsonVar);
		}
		

	}
	
	//https://hostname/flight/flightNumber
	@RequestMapping(value="/flight/{flight_number}", method=RequestMethod.GET)
	public ResponseEntity<E> getFlightWithJSONReq(@PathVariable("flight_number") String flightNumber,@RequestParam(value="xml",defaultValue="false",required=false) boolean xmlFlag,HttpServletResponse response) throws Exception {
			
			Flight flight = flightDAO.findOne(flightNumber);
			if(!xmlFlag){
				if(flight==null){
					URI location = ServletUriComponentsBuilder
				            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "404").queryParam("msg", "Sorry, the requested flight with flight number " + flightNumber + " does not exist").build().toUri();

					return redirectTo(location);
				}
				else{
					JSONObject flightObj = new JSONObject();
					flightObj.put("flightNumber", flight.getNumber());
					flightObj.put("price", flight.getPrice());
					flightObj.put("from", flight.getFrom());
					flightObj.put("to", flight.getTo());
					flightObj.put("departureTime", flight.getDepartureTime());
					flightObj.put("arrivalTime", flight.getArrivalTime());
					flightObj.put("description", flight.getDescription());
					flightObj.put("seatsLeft", flight.getSeatsLeft());
					
					JSONObject planeObj = new JSONObject();
					planeObj.put("capacity",flight.getPlane().getCapacity());
					planeObj.put("model",flight.getPlane().getModel());
					planeObj.put("manufacturer",flight.getPlane().getManufacturer());
					planeObj.put("yearOfManufacture",flight.getPlane().getYearOfManufacture());
					flightObj.put("plane", planeObj);
					
					JSONObject listOfPassengers = new JSONObject();
					listOfPassengers.put("passenger", flight.getPassengers());
					System.out.println(flight.getPassengers().size());
				
					flightObj.put("passengers", listOfPassengers);
					JSONObject returnJsonVar = new JSONObject();
					returnJsonVar.put("flight", flightObj);
						
					return convertToJSON(returnJsonVar);
					
				}

			}
			else{
				URI location = ServletUriComponentsBuilder
			            .fromCurrentServletMapping().path("/flightXML/"+flightNumber).build().toUri();

				return redirectTo(location);
			}		

	}

	//https://hostname/flight/flightNumber?price=120&from=AA&to=BB&departureTime=CC&arrivalTime=DD&description=EE&capacity=GG&model=HH&manufacturer=II&yearOfManufacture=1997
	@RequestMapping(value="/flight/{flight_number}",method = RequestMethod.POST)
	public ResponseEntity<E> createOrUpdateFlight(@PathVariable(value = "flight_number") String flightNumber, @RequestParam(value="price") int price, @RequestParam(value="from") String from, @RequestParam(value="to") String to, @RequestParam(value="departureTime") String departureTime, @RequestParam(value="arrivalTime") String arrivalTime,@RequestParam(value="description") String description,@RequestParam(value="capacity") int capacity,@RequestParam(value="model") String model,@RequestParam(value="manufacturer") String manufacturer,@RequestParam(value="yearOfManufacture") int yearOfManufacture) throws ParseException{
		
		Plane plane = new Plane(capacity,model,manufacturer,yearOfManufacture);
		Flight flight = new Flight(flightNumber, price, from, to, (Date) new SimpleDateFormat("yyyy-MM-dd-hh").parse(departureTime), (Date) new SimpleDateFormat("yyyy-MM-dd-hh").parse(arrivalTime), capacity, description, plane, null);
		
		try{
			flightDAO.save(flight);
			URI location = ServletUriComponentsBuilder
		            .fromCurrentServletMapping().path("/flight/{flight_number}").queryParam("xml", true).build().expand(flightNumber).toUri();
			System.out.println(location);
			return redirectTo(location);
			//return flight;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value="/airline/{flight_number}",method=RequestMethod.DELETE)
	public ResponseEntity<E> deleteFlight(@PathVariable(value = "flight_number")String flightNumber){
		
		try {
				Flight flight = new Flight();
				flight.setNumber(flightNumber);
				flight = flightDAO.findOne(flightNumber);
				if(flight==null){
					URI location = ServletUriComponentsBuilder
				            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "404").queryParam("msg", "Sorry, the requested flight with flight number " + flightNumber + " does not exist").build().toUri();

					return redirectTo(location);

				}
				if(!flight.getPassengers().isEmpty()){
					
					System.out.println("Need to send 400. Reservations exist.");
					URI location = ServletUriComponentsBuilder
				            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "400").queryParam("msg", "You can not delete a flight that has one or more reservation").build().toUri();

					return redirectTo(location);
				}
				else{
					flightDAO.delete(flight);
					URI location = ServletUriComponentsBuilder
				            .fromCurrentServletMapping().path("/applicationErrorInXML").queryParam("code", "200").queryParam("msg", "Flight with id " + flightNumber + " is deleted successfully").build().toUri();

					
					return redirectTo(location);

					
					//return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can not delete a flight that has one or more reservation");
				}
				
		} catch (Exception e) {
			URI location = ServletUriComponentsBuilder
		            .fromCurrentServletMapping().path("/applicationError").queryParam("code", "500").queryParam("msg", "Something went wrong").build().toUri();
			
			e.printStackTrace();
			return redirectTo(location);
		}
		
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
	
	public ResponseEntity convertToJSON(JSONObject returnJsonVar){
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(returnJsonVar.toString());
		String prettyJsonString = gson.toJson(je);
		
		return new ResponseEntity(prettyJsonString,HttpStatus.OK);
	}
}
