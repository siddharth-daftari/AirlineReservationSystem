package edu.sjsu.cmpe275.lab2.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//@Table(name="PLANE")
@Embeddable
public class Plane {
		
		/*@Id
		@Column(name="PLANE_ID")
		@GeneratedValue(strategy=GenerationType.AUTO)
		private long planeId;*/
		
		@Column(name="CAPACITY")
		private int capacity;
		public Plane(int capacity, String model, String manufacturer, int yearOfManufacture) {
			super();
			this.capacity = capacity;
			this.model = model;
			this.manufacturer = manufacturer;
			this.yearOfManufacture = yearOfManufacture;
		}
		@Column(name="MODEL")
	    private String model;
		@Column(name="MANUFACTURER")
	    private String manufacturer;
		@Column(name="YEAROFMANUFACTURE")
	    private int yearOfManufacture;
		
		public Plane() {
		}
		
		
		public int getCapacity() {
			return capacity;
		}
		public void setCapacity(int capacity) {
			this.capacity = capacity;
		}
		public String getModel() {
			return model;
		}
		public void setModel(String model) {
			this.model = model;
		}
		public String getManufacturer() {
			return manufacturer;
		}
		public void setManufacturer(String manufacturer) {
			this.manufacturer = manufacturer;
		}
		public int getYearOfManufacture() {
			return yearOfManufacture;
		}
		public void setYearOfManufacture(int yearOfManufacture) {
			this.yearOfManufacture = yearOfManufacture;
		}
	    

}
