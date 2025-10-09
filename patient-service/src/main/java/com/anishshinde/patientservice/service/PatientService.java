package com.anishshinde.patientservice.service;

import com.anishshinde.patientservice.dto.PatientRequestDto;
import com.anishshinde.patientservice.dto.PatientResponseDto;
import com.anishshinde.patientservice.exception.EmailAlreadyExistsException;
import com.anishshinde.patientservice.exception.PatientNotFoundException;
import com.anishshinde.patientservice.mapper.PatientMapper;
import com.anishshinde.patientservice.model.Patient;
import com.anishshinde.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<PatientResponseDto> getPatients(){
        List<Patient> patients = patientRepository.findAll();
        return patients
                .stream()
                .map(PatientMapper::toDto)
                .toList();
    }

    public PatientResponseDto getPatientById(UUID id) {
        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException("Patient with given id not found: " + id));
        return PatientMapper.toDto(patient);
    }

    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto){
        if(patientRepository.existsByEmail(patientRequestDto.getEmail())){
            throw new EmailAlreadyExistsException(patientRequestDto.getEmail());
        }

        Patient patient = patientRepository.save(PatientMapper.toModel(patientRequestDto));
        return PatientMapper.toDto(patient);
    }

    public PatientResponseDto updatePatient(UUID id, PatientRequestDto patientRequestDto) {
        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException("Patient with given id not found: " + id));

        if(patientRepository.existsByEmailAndIdNot(patientRequestDto.getEmail(), id)){
            throw new EmailAlreadyExistsException(patientRequestDto.getEmail());
        }

        patient.setName(patientRequestDto.getName());
        patient.setEmail(patientRequestDto.getEmail());
        patient.setAddress(patientRequestDto.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDto.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDto(updatedPatient);
    }

    public void deletePatient(UUID id) {
        if(!patientRepository.existsById(id)){
            throw new PatientNotFoundException("Patient with given id not found: " + id);
        }
        patientRepository.deleteById(id);
    }
}
