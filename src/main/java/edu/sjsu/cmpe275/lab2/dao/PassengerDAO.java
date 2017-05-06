package edu.sjsu.cmpe275.lab2.dao;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.sjsu.cmpe275.lab2.model.Passenger;

/**
 * @author siddharth and parvez
 *
 */
@Repository
public interface PassengerDAO extends CrudRepository<Passenger, String> {
	/**
	 * @param phone
	 * @return
	 */
	List<Passenger> findByPhone(String phone);
	/**
	 * @param id
	 * @return
	 */
	List<Passenger> findById(String id);
	
}
