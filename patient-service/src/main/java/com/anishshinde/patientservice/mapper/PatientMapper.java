package com.anishshinde.patientservice.mapper;

import com.anishshinde.patientservice.dto.PatientRequestDto;
import com.anishshinde.patientservice.dto.PatientResponseDto;
import com.anishshinde.patientservice.model.Patient;

import java.time.LocalDate;

public class PatientMapper {

    public static PatientResponseDto toDto(Patient patient){
        PatientResponseDto patientResponseDTO = new PatientResponseDto();
        patientResponseDTO.setId(patient.getId().toString());
        patientResponseDTO.setName(patient.getName());
        patientResponseDTO.setEmail(patient.getEmail());
        patientResponseDTO.setAddress(patient.getAddress());
        patientResponseDTO.setDateOfBirth(patient.getDateOfBirth().toString());
        return patientResponseDTO;
    }

    public static Patient toModel(PatientRequestDto patientRequestDto){
        Patient patient = new Patient();
        patient.setName(patientRequestDto.getName());
        patient.setEmail(patientRequestDto.getEmail());
        patient.setAddress(patientRequestDto.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDto.getDateOfBirth()));
        patient.setRegistrationDate(LocalDate.parse(patientRequestDto.getRegistrationDate()));
        return patient;
    }

}
