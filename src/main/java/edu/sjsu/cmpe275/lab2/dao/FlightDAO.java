package edu.sjsu.cmpe275.lab2.dao;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.sjsu.cmpe275.lab2.model.Flight;

/**
 * @author siddharth and parvez
 *
 */
@Repository
public interface FlightDAO extends CrudRepository<Flight,String>{
	/**
	 * @param number
	 * @return
	 */
	Flight findByNumber(String number);

	/**
	 * @param spec
	 * @return
	 */
	List<Flight> findAll(Specification spec);
}
