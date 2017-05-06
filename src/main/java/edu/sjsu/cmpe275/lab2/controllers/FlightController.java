package edu.sjsu.cmpe275.lab2.controllers;


import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.springframework.transaction.annotation.Transactional;
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

import edu.sjsu.cmpe275.lab2.dao.FlightDAO;
import edu.sjsu.cmpe275.lab2.dao.ReservationDAO;
import edu.sjsu.cmpe275.lab2.model.CustomException;
import edu.sjsu.cmpe275.lab2.model.Flight;
import edu.sjsu.cmpe275.lab2.model.Flight_;
import edu.sjsu.cmpe275.lab2.model.Plane;
import edu.sjsu.cmpe275.lab2.model.Reservation;
import edu.sjsu.cmpe275.lab2.model.Reservation_;

/**
 * @author siddharth and parvez
 *
 * @param <E>
 */
@RestController
public class FlightController<E> {
	
	@Autowired
	private FlightDAO flightDAO;
	
	@Autowired
	private ReservationDAO reservationDAO;
	
	/**
	 * @param location
	 * @return
	 */
	public ResponseEntity<E> redirectTo(URI location){
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(location);
		return (ResponseEntity<E>) new ResponseEntity<Void>(headers, HttpStatus.MOVED_PERMANENTLY);
	}
	
	
	//https://hostname/flight/flightNumber
	/**
	 * @param flightNumber
	 * @param xmlFlag
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@Transactional
	@RequestMapping(value="/flight/{flight_number}", method=RequestMethod.GET)
	public ResponseEntity<E> getFlightWithJSONReq(@PathVariable("flight_number") String flightNumber,@RequestParam(value="xml",defaultValue="false",required=false) boolean xmlFlag,HttpServletResponse response) throws Exception {
			
			Flight flight = flightDAO.findOne(flightNumber);
			JSONObject returnJsonVar = new JSONObject();
			
			if(flight==null){
				throw new CustomException("404", "Sorry, the requested flight with flight number " + flightNumber + " does not exist");
			}
			else{
				JSONObject flightObj = new JSONObject();
				flightObj.put("flightNumber", flight.getNumber());
				flightObj.put("price", flight.getPrice());
				flightObj.put("from", flight.getFrom());
				flightObj.put("to", flight.getTo());
				flightObj.put("departureTime", new SimpleDateFormat("yyyy-MM-dd-hh").format(flight.getDepartureTime()));
				flightObj.put("arrivalTime", new SimpleDateFormat("yyyy-MM-dd-hh").format(flight.getArrivalTime()));
				flightObj.put("description", flight.getDescription());
				flightObj.put("seatsLeft", Integer.toString(flight.getSeatsLeft()));
				
				JSONObject planeObj = new JSONObject();
				planeObj.put("capacity",Integer.toString(flight.getPlane().getCapacity()));
				planeObj.put("model",flight.getPlane().getModel());
				planeObj.put("manufacturer",flight.getPlane().getManufacturer());
				planeObj.put("yearOfManufacture",flight.getPlane().getYearOfManufacture());
				flightObj.put("plane", planeObj);
				
				JSONObject listOfPassengers = new JSONObject();
				listOfPassengers.put("passenger", flight.getPassengers());
				System.out.println(flight.getPassengers().size());
			
				flightObj.put("passengers", listOfPassengers);
				
				returnJsonVar.put("flight", flightObj);
				
			}
			if(!xmlFlag){
				return convertToJSON(returnJsonVar);
			}
	
			else{
				return convertToXml(returnJsonVar);
			}		

	}

	//https://hostname/flight/flightNumber?price=120&from=AA&to=BB&departureTime=CC&arrivalTime=DD&description=EE&capacity=GG&model=HH&manufacturer=II&yearOfManufacture=1997
	/**
	 * @param flightNumber
	 * @param price
	 * @param from
	 * @param to
	 * @param departureTime
	 * @param arrivalTime
	 * @param description
	 * @param capacity
	 * @param model
	 * @param manufacturer
	 * @param yearOfManufacture
	 * @return
	 * @throws ParseException
	 */
	@Transactional
	@RequestMapping(value="/flight/{flight_number}",method = RequestMethod.POST)
	public ResponseEntity<E> createOrUpdateFlight(@PathVariable(value = "flight_number") String flightNumber, @RequestParam(value="price") int price, @RequestParam(value="from") String from, @RequestParam(value="to") String to, @RequestParam(value="departureTime") String departureTime, @RequestParam(value="arrivalTime") String arrivalTime,@RequestParam(value="description") String description,@RequestParam(value="capacity") int capacity,@RequestParam(value="model") String model,@RequestParam(value="manufacturer") String manufacturer,@RequestParam(value="yearOfManufacture") int yearOfManufacture) throws ParseException{
		
		Plane plane = new Plane(capacity,model,manufacturer,yearOfManufacture);
		Flight flight = flightDAO.findOne(flightNumber); 
		int seatsLeft = 0;
		URI location;
		if(flight==null){
			 flight = new Flight(flightNumber, price, from, to, (Date) new SimpleDateFormat("yyyy-MM-dd-hh").parse(departureTime), (Date) new SimpleDateFormat("yyyy-MM-dd-hh").parse(arrivalTime), capacity, description, plane, null);
		}else{
			//set the price. It does not affect the previous reservations. 
			seatsLeft = flight.getSeatsLeft();
			System.out.println("Flight exists. So updating");
			
			
			//set the plane. check the capacity of the plane
			//if capacity is increasing add the difference in the seatsLeft
			//if reducing, check. reservations < new capacity == true.
			
			int originalCapacity = flight.getPlane().getCapacity();
			int activeReservations = originalCapacity-flight.getSeatsLeft();
			if(capacity >= originalCapacity){
				seatsLeft = capacity-activeReservations;
				/*flight.getPlane().setCapacity(capacity);
				flight.setSeatsLeft(capacity-activeReservations);*/
				System.out.println("Capcity greater than or equal to original");
			}
			else{
				
					System.out.println("Capcity less than original");
					if(capacity<flight.getPassengers().size()){
						//throw exception.
						System.out.println("Capcity less than res");
						throw new CustomException("400", "Active reservation count for this flight is higher than the target capacity");
					}
					else{
						seatsLeft = capacity-activeReservations;
						/*flight.getPlane().setCapacity(capacity);
						flight.setSeatsLeft(capacity-activeReservations);*/
					}
				
			}
			
			//check for overlapping time 
			Specification spec = new Specification<Reservation>() {
			    
				@Override
				public Predicate toPredicate(Root<Reservation> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
					
					List<Predicate> predicates = new ArrayList<>();
					
					Join<Reservation,Flight> joinRoot = root.join(Reservation_.flights);
					
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
			
			for(Reservation reservation : reservationList){
				List<Flight> flightList = reservation.getFlights();
				Date newArrivalTime = (Date) (new SimpleDateFormat("yyyy-MM-dd-hh").parse(arrivalTime));
				Date newDepartureTime = (Date) (new SimpleDateFormat("yyyy-MM-dd-hh").parse(departureTime));
				
				for(Flight flightTemp : flightList){
					
					if(!flightNumber.equalsIgnoreCase(flightTemp.getNumber())){
						if(newArrivalTime.before(flightTemp.getDepartureTime()) || newDepartureTime.after(flightTemp.getArrivalTime())){
							
						}else{
						
							throw new CustomException("400", "Cannot update the flight. Updated time interval is in conflict with existing reservations.");
						}
					}
				}
			}
		}
		
		flight.setPrice(price);
		flight.setFrom(from);
		flight.setTo(to);
		flight.setDepartureTime(new SimpleDateFormat("yyyy-MM-dd-hh").parse(departureTime));
		flight.setArrivalTime(new SimpleDateFormat("yyyy-MM-dd-hh").parse(arrivalTime));
		flight.setDescription(description);
		flight.getPlane().setCapacity(capacity);
		flight.getPlane().setModel(model);
		flight.getPlane().setManufacturer(manufacturer);
		flight.getPlane().setYearOfManufacture(yearOfManufacture);
		flight.setSeatsLeft(seatsLeft);
		
		flightDAO.save(flight);
		location = ServletUriComponentsBuilder
	            .fromCurrentServletMapping().path("/flight/{flight_number}").queryParam("xml", true).build().expand(flightNumber).toUri();
		
		return redirectTo(location);
		//return flight;
		
	}
	
	/**
	 * @param flightNumber
	 * @return
	 */
	@Transactional
	@RequestMapping(value="/airline/{flight_number}",method=RequestMethod.DELETE)
	public ResponseEntity<E> deleteFlight(@PathVariable(value = "flight_number")String flightNumber){
		
		try {
				Flight flight = new Flight();
				flight.setNumber(flightNumber);
				flight = flightDAO.findOne(flightNumber);
				if(flight==null){
					throw new CustomException("404", "Sorry, the requested flight with flight number " + flightNumber + " does not exist");
				}
				if(!flight.getPassengers().isEmpty()){
					
					throw new CustomException("400", "You can not delete a flight that has one or more reservation");
				}
				else{
					flightDAO.delete(flight);
					URI location = ServletUriComponentsBuilder
				            .fromCurrentServletMapping().path("/applicationErrorInXML").queryParam("code", "200").queryParam("msg", "Flight with id " + flightNumber + " is deleted successfully").build().toUri();

					
					return redirectTo(location);

					
					//return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can not delete a flight that has one or more reservation");
				}
				
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException("500", "Something went wrong");
		}
		
	}
	
	/**
	 * @param returnJsonVar
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
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
	
	/**
	 * @param returnJsonVar
	 * @return
	 */
	public ResponseEntity convertToJSON(JSONObject returnJsonVar){
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(returnJsonVar.toString());
		String prettyJsonString = gson.toJson(je);
		
		return new ResponseEntity(prettyJsonString,HttpStatus.OK);
	}
	
	/**
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
