package edu.sjsu.cmpe275.lab2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.cmpe275.lab2.dao.PassengerDAO;
import edu.sjsu.cmpe275.lab2.model.Passenger;

@RestController
public class PassengerController {
	
	@Autowired
	private PassengerDAO passengerDAO; 
	
	Passenger passenger = null;

	@RequestMapping("/passenger")
    public @ResponseBody Passenger createPassenger(@RequestParam(value="firstname", defaultValue="firstname") String firstname, @RequestParam(value="lastname", defaultValue="lastname") String lastname, @RequestParam(value="age", defaultValue="0") int age, @RequestParam(value="gender", defaultValue="gender") String gender, @RequestParam(value="phone", defaultValue="phone") String phone) {
		try {
			passenger = new Passenger(firstname,lastname, age, gender, phone);
			passengerDAO.save(passenger);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return passenger;
		
	}
}
