package edu.sjsu.cmpe275.lab2.dao;


import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.sjsu.cmpe275.lab2.model.Passenger;
import edu.sjsu.cmpe275.lab2.model.Reservation;

/**
 * @author siddharth and parvez
 *
 */
@Repository
public interface ReservationDAO extends CrudRepository<Reservation, String> {
	
	/**
	 * @param spec
	 * @return
	 */
	<T> List<Reservation> findAll(Specification<T> spec);
	
	/**
	 * @param passenger
	 * @return
	 */
	List<Reservation> findByPassenger(Passenger passenger);
	
}