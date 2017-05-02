package edu.sjsu.cmpe275.lab2.model;

import javax.persistence.metamodel.*;

@StaticMetamodel(Reservation.class)
public class Reservation_ {
    public static volatile SingularAttribute<Reservation, String> orderNumber;
    public static volatile SingularAttribute<Reservation, Passenger> passenger;
    public static volatile SingularAttribute<Reservation, Integer> price;
    public static volatile ListAttribute<Reservation, Flight> flights;
}
