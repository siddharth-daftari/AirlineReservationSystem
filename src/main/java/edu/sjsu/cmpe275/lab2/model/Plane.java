package edu.sjsu.cmpe275.lab2.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

//@Table(name="PLANE")
/**
 * @author siddharth and parvez
 *
 */
@Embeddable
public class Plane {
		
		/*@Id
		@Column(name="PLANE_ID")
		@GeneratedValue(strategy=GenerationType.AUTO)
		private long planeId;*/
		
		@Column(name="CAPACITY")
		private int capacity;
		/**
		 * @param capacity
		 * @param model
		 * @param manufacturer
		 * @param yearOfManufacture
		 */
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
		
		/**
		 * 
		 */
		public Plane() {
		}
		
		
		/**
		 * @return
		 */
		public int getCapacity() {
			return capacity;
		}
		/**
		 * @param capacity
		 */
		public void setCapacity(int capacity) {
			this.capacity = capacity;
		}
		/**
		 * @return
		 */
		public String getModel() {
			return model;
		}
		/**
		 * @param model
		 */
		public void setModel(String model) {
			this.model = model;
		}
		/**
		 * @return
		 */
		public String getManufacturer() {
			return manufacturer;
		}
		/**
		 * @param manufacturer
		 */
		public void setManufacturer(String manufacturer) {
			this.manufacturer = manufacturer;
		}
		/**
		 * @return
		 */
		public int getYearOfManufacture() {
			return yearOfManufacture;
		}
		/**
		 * @param yearOfManufacture
		 */
		public void setYearOfManufacture(int yearOfManufacture) {
			this.yearOfManufacture = yearOfManufacture;
		}
	    

}
