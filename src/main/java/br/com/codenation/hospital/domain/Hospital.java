package br.com.codenation.hospital.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="hospital_collection")
// https://codeboje.de/spring-data-mongodb/
// https://lishman.io/object-mapping-with-spring-data-mongodb
public class Hospital implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id;
	private String hospitalName;
	private String address;
	private int beds;
	private int availableBeds;
	
	
	//só serão acessados se forem carregados
	//Using Collection References
	@DBRef(lazy=true) 
	private List<Patient> patients = new ArrayList<Patient>();
	
	//Using Collection References
	@DBRef(lazy=true)
	private List<Product> products = new ArrayList<Product>();

	public Hospital() {
		
	}

	public Hospital(String id) {
		super();
		this.id = id;
	}
	
	public Hospital(String id, String name, String address, int beds, int availableBeds) {
		super();
		this.id = id;
		this.hospitalName = name;
		this.address = address;
		this.beds = beds;
		this.availableBeds = availableBeds;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return hospitalName;
	}

	public void setName(String name) {
		this.hospitalName = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getBeds() {
		return beds;
	}

	public void setBeds(int beds) {
		this.beds = beds;
	}

	public int getAvailableBeds() {
		return availableBeds;
	}

	public void setAvailableBeds(int availableBeds) {
		if(availableBeds < 0){
			throw new RuntimeException("Não possui leitos disponiveis");
		}
		this.availableBeds = availableBeds;
	}

	public List<Patient> getPatients() {
		return patients;
	}

	public boolean addPacient(Patient patient){
		if(availableBeds > 0 && !patients.contains(patient)) {
			patient.setHospital(this);
			patient.setEntryDate(new Date());
			patient.setActive(true);
			patient.setExitDate(null);
			this.availableBeds--;
			return true;
		}
		return false;
	}

	public void removePacient(Patient patient){
		availableBeds++;
		patient.setActive(false);
		patient.setExitDate(new Date());
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
	public void setProduct(Product product) {
		this.products.add(product);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + availableBeds;
		result = prime * result + beds;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((hospitalName == null) ? 0 : hospitalName.hashCode());
		result = prime * result + ((patients == null) ? 0 : patients.hashCode());
		result = prime * result + ((products == null) ? 0 : products.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hospital other = (Hospital) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (availableBeds != other.availableBeds)
			return false;
		if (beds != other.beds)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (hospitalName == null) {
			if (other.hospitalName != null)
				return false;
		} else if (!hospitalName.equals(other.hospitalName))
			return false;
		if (patients == null) {
			if (other.patients != null)
				return false;
		} else if (!patients.equals(other.patients))
			return false;
		if (products == null) {
			if (other.products != null)
				return false;
		} else if (!products.equals(other.products))
			return false;
		return true;
	}
}