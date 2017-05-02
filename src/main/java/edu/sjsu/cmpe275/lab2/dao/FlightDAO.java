package edu.sjsu.cmpe275.lab2.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.sjsu.cmpe275.lab2.model.Flight;

@Repository
@Transactional
public interface FlightDAO extends CrudRepository<Flight,String>{
}
