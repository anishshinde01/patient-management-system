package com.anishshinde.patientservice.service;

import com.anishshinde.patientservice.model.Patient;
import com.anishshinde.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<Patient> getPatients(){
        return patientRepository.findAll();
    }

    public ResponseEntity<?> getPatientById(UUID id) {
        Optional<Patient> patient = patientRepository.findById(id);
        if(patient.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Invalid Patient ID");
        }
        return ResponseEntity.ok(patient);
    }
}
