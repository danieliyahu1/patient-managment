package com.pm.patient_service.controller;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.dto.validators.CreatePatientValidationGroup;
import com.pm.patient_service.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Tag(name="Patient", description = "Patient API")
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    @Operation(summary = "Get all patients")
    public ResponseEntity<List<PatientResponseDTO>> getPatients() {
        return ResponseEntity.ok().body(patientService.getPatient());
    }

    @PostMapping
    @Operation(summary = "Create a new patient")
    public ResponseEntity<PatientResponseDTO> createPatient(@Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody PatientRequestDTO patientRequestDTO){
        return ResponseEntity.ok().body(patientService.createPatient(patientRequestDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing patient")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable UUID id, @Validated({CreatePatientValidationGroup.class}) @RequestBody PatientRequestDTO patientRequestDTO)
    {
        return ResponseEntity.ok().body(patientService.updatePatient(id, patientRequestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient")
    public ResponseEntity<PatientResponseDTO> deletePatient(@PathVariable UUID id) {
        return ResponseEntity.ok().body(patientService.deletePatient(id));
    }
}
