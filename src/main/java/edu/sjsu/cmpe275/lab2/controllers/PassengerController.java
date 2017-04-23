package edu.sjsu.cmpe275.lab2.controllers;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.hibernate.Query;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import edu.sjsu.cmpe275.lab2.dao.PassengerDAO;
import edu.sjsu.cmpe275.lab2.dao.ReservationDAO;
import edu.sjsu.cmpe275.lab2.model.Passenger;
import edu.sjsu.cmpe275.lab2.model.Reservation;

@RestController
public class PassengerController {
	
	@Autowired
	private PassengerDAO passengerDAO; 
	
	@Autowired
	private ReservationDAO reservationDAO; 
	
	@RequestMapping("/passenger")
    public ModelAndView createPassenger(@RequestParam(value="firstname", defaultValue="firstname") String firstname, @RequestParam(value="lastname", defaultValue="lastname") String lastname, @RequestParam(value="age", defaultValue="0") int age, @RequestParam(value="gender", defaultValue="gender") String gender, @RequestParam(value="phone", defaultValue="phone") String phone, ModelMap model) throws Exception {
		Passenger passenger = null;
		
		//check if unique phone number
		List<Passenger> listOfPassengers = passengerDAO.findByPhone(phone);
		
		if(listOfPassengers!=null && !listOfPassengers.isEmpty()){
			throw new Exception("another passenger with the same number already exists.");
		}else{
			passenger = new Passenger(firstname,lastname, age, gender, phone);
			passengerDAO.save(passenger);
		}
		//return "redirect:/passenger/" + passenger.getId();
		return new ModelAndView("forward:/passenger/" + passenger.getId() + "?json=true", model);
		
	}
	
	@RequestMapping(value="/passenger/{id}")
    public @ResponseBody Map<String, Object> getPassengerWithJsonReq(@PathVariable("id") String id, @RequestParam(value="json", defaultValue="false") boolean isJsonReq, @RequestParam(value="xml", defaultValue="false") boolean isXmlReq, HttpServletResponse response) throws Exception {
		if(isJsonReq){
		
			Passenger passengerTemp = null;
			
			//check if unique phone number
			List<Passenger> listOfPassengers = passengerDAO.findById(id);
			
			if(listOfPassengers==null || listOfPassengers.isEmpty()){
				throw new Exception("cannot find passenger with ID: " + id);
			}else{
				passengerTemp = listOfPassengers.get(0);
				
				JSONObject returnJsonVar = new JSONObject();
				
				List<Reservation> listOfReservation = reservationDAO.findByPassenger(passengerTemp);
				
				JSONObject passenger = new JSONObject();
				passenger.put("id", passengerTemp.getId());
				passenger.put("firstname", passengerTemp.getFirstname());
				passenger.put("lastname", passengerTemp.getLastname());
				passenger.put("age", passengerTemp.getAge());
				passenger.put("gender", passengerTemp.getGender());
				passenger.put("phone", passengerTemp.getPhone());
				
				JSONObject reservation = new JSONObject();
				reservation.put("reservation", listOfReservation);
				
				passenger.put("reservations", reservation);
				
				returnJsonVar.put("passenger", passenger);
				return returnJsonVar.toMap();
				
			}
		}else{
			response.sendRedirect("/passengerWithXml/"+id);
			return (new JSONObject()).toMap();
		}
	}
	
	@RequestMapping(value="/passengerWithXml/{id}")
    public @ResponseBody String getPassengerWithXmlReq(@PathVariable("id") String id) throws Exception {
		Passenger passengerTemp = null;
		System.out.println("XML wadi method ma");
		
		//check if unique phone number
		List<Passenger> listOfPassengers = passengerDAO.findById(id);
		
		if(listOfPassengers==null || listOfPassengers.isEmpty()){
			throw new Exception("cannot find passenger with ID: " + id);
		}else{
			passengerTemp = listOfPassengers.get(0);
			
			JSONObject returnJsonVar = new JSONObject();
			
			List<Reservation> listOfReservation = reservationDAO.findByPassenger(passengerTemp);
			
			JSONObject passenger = new JSONObject();
			passenger.put("id", passengerTemp.getId());
			passenger.put("firstname", passengerTemp.getFirstname());
			passenger.put("lastname", passengerTemp.getLastname());
			passenger.put("age", passengerTemp.getAge());
			passenger.put("gender", passengerTemp.getGender());
			passenger.put("phone", passengerTemp.getPhone());
			
			JSONObject reservation = new JSONObject();
			reservation.put("reservation", listOfReservation);
			
			passenger.put("reservations", reservation);
			
			returnJsonVar.put("passenger", passenger);
			
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
            
            return stringWriter.toString();
			
		}
	}
	
	@ExceptionHandler
	void handleException(Exception e, HttpServletResponse response) throws IOException {
		System.out.println("Error: --> " + e.getMessage());
	   	response.sendRedirect("/applicationError?" + "code=400&msg=" + e.getMessage());
	}
	
}
