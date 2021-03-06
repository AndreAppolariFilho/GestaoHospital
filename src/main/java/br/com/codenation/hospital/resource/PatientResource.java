package br.com.codenation.hospital.resource;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;


import br.com.codenation.hospital.dto.PatientDTO;
import br.com.codenation.hospital.resource.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.codenation.hospital.constant.Constant;
import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.domain.Patient;
import br.com.codenation.hospital.services.PatientService;
import br.com.codenation.hospital.services.HospitalService;

@RestController
@RequestMapping(path = Constant.V1Path)
public class PatientResource {

	@Autowired
	private PatientService service;
	
	@Autowired
	private HospitalService hospitalService;


	private boolean patientInHospital(Hospital hospital, Patient patient){
		return hospital.getPatients().contains(patient);

	}

	@PostMapping(path="/pacientes/", produces="application/json")
	public ResponseEntity<Patient> createPacient(@PathVariable("hospital_id") String hospital_id, @RequestBody Patient objP){
		Hospital obj = hospitalService.findById(hospital_id);
		List<Patient> patients = obj.getPatients();
		patients.add(objP);
		service.update(objP);
		hospitalService.update(obj);
		return ResponseEntity.ok().body(objP);
	}

	@PutMapping(path="/pacientes/{paciente}", produces="application/json")
	public ResponseEntity<Patient> createPacient(@PathVariable("hospital_id") String hospital_id, @PathVariable("paciente") String patient_id, @RequestBody Patient objP){
		Hospital obj = hospitalService.findById(hospital_id);
		List<Patient> patients = obj.getPatients();

		Patient p = service.findById(patient_id);
		if(!patientInHospital(obj, p))
			throw new RuntimeException("Esse paciente não esta cadastrado neste hospital");
		patients.set(patients.indexOf(p), objP);
		service.update(objP);
		hospitalService.update(obj);
		return ResponseEntity.ok().body(objP);
	}

	@PatchMapping(path="/pacientes/{paciente}", produces="application/json")
	public ResponseEntity<Patient> checkIn(@PathVariable("hospital_id") String hospital_id, @PathVariable("paciente") String patient_id, @RequestBody String data) {
		Hospital obj = hospitalService.findById(hospital_id);

		Patient p = service.findById(patient_id);
		if (!patientInHospital(obj, p))
			throw new RuntimeException("Esse paciente não esta cadastrado neste hospital");
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		try {
			map = mapper.readValue(data, new TypeReference<Map<String, String>>() {
			});
			if (map.get("action").equals("check-in")) {
				if (!p.isActive()) {
					obj.setAvailableBeds(obj.getAvailableBeds() - 1);
					hospitalService.checkIn(obj, p);
				}
			} else if (map.get("action").equals("check-out")) {
				if (p.isActive()) {
					obj.setAvailableBeds(obj.getAvailableBeds() + 1);
					hospitalService.checkOut(obj, p.getId());
				}
			}
			hospitalService.update(obj);
			service.update(p);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok().body(p);
	}


	@GetMapping(path="/pacientes")
	public List<Patient> findPatients(@PathVariable String hospital_id){
		Hospital obj = hospitalService.findById(hospital_id);
		List<Patient> patientList = obj.getPatients();

		if(patientList != null){
			return patientList;
		}
		throw new ResourceNotFoundException("Hospital sem pacientes!");
	}

	@GetMapping(path="/pacientes/{idPaciente}")
	public Patient findPatientById(@PathVariable String hospital_id, @PathVariable String idPaciente){
		Hospital obj = hospitalService.findById(hospital_id);
		List<Patient> patientList = obj.getPatients();
		if (patientList != null) {
			return  patientList.stream()
					.filter(patientFilter -> patientFilter.getId().trim().equals(idPaciente))
					.findFirst()
					.orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado!"));

		}
		throw new ResourceNotFoundException("Hospital sem pacientes!");
	}

	@PostMapping(path="/pacientes/checkin", produces="application/json")
	public Patient checkinPacient(@PathVariable("hospital_id") String idHospital, @RequestBody Patient patient){
		Hospital hospital = hospitalService.findById(idHospital);
		return hospitalService.checkIn(hospital, patient);
	}

	@PostMapping(path="/pacientes/checkout", produces="application/json")
	public Patient checkinPacient(@PathVariable("hospital_id") String idHospital, @RequestBody String idPatient){
		Hospital hospital = hospitalService.findById(idHospital);
		return hospitalService.checkOut(hospital, idPatient);
	}
}