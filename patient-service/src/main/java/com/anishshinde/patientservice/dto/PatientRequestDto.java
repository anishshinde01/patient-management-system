package com.anishshinde.patientservice.dto;

import com.anishshinde.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PatientRequestDto {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String address;

    @NotBlank(message = "Date of Birth is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in yyyy-MM-dd")
    private String dateOfBirth;

    // registrationDate is only required on create-patient request and not on update-patient
    // once registered, registrationDate can no longer be modified
    @NotBlank(groups = CreatePatientValidationGroup.class, message = "Date of Registration is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in yyyy-MM-dd")
    private String registrationDate;

}
