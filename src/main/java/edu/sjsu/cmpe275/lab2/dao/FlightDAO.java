package edu.sjsu.cmpe275.lab2.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import edu.sjsu.cmpe275.lab2.model.Flight;

public interface FlightDAO extends CrudRepository<Flight,String>{
		
}
