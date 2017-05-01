package edu.sjsu.cmpe275.lab2.dao;


import org.hibernate.annotations.Columns;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import edu.sjsu.cmpe275.lab2.model.Reservation;
import edu.sjsu.cmpe275.lab2.model.ReservationForGetPassengers;

import java.lang.String;
import java.util.List;

import javax.persistence.Column;

import edu.sjsu.cmpe275.lab2.model.Passenger;

@Repository
@Transactional
public interface ReservationDAO extends CrudRepository<Reservation, String> {
	
	List<Reservation> findByPassenger(Passenger passenger);
	
	
}