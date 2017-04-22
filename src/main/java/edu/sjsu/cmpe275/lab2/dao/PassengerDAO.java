package edu.sjsu.cmpe275.lab2.dao;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.sjsu.cmpe275.lab2.model.Passenger;

@Repository
@Transactional
public interface PassengerDAO extends CrudRepository<Passenger, Long> {
	
}
