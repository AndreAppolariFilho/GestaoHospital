package br.com.codenation.hospital.dto;

import java.io.Serializable;
import br.com.codenation.hospital.domain.Hospital;

public class HospitalDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String hospitalName;
	private String address;
	private int beds;
	private int availableBeds;
	
	public HospitalDTO() {
		
	}
	
	public HospitalDTO(Hospital obj) {
		this.id = obj.getId();
		this.hospitalName = obj.getName();
		this.address = obj.getAddress();
		this.beds = obj.getBeds();
		this.availableBeds = obj.getAvailableBeds();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
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
		this.availableBeds = availableBeds;
	}
}