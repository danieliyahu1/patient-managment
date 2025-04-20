package com.pm.patient_service.service;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.exception.EmailAlreadyExistsException;
import com.pm.patient_service.exception.PatientNotFoundException;
import com.pm.patient_service.grpc.BillingServiceGrpcClient;
import com.pm.patient_service.kafka.KafkaProducer;
import com.pm.patient_service.mapper.PatientMapper;
import com.pm.patient_service.model.Patient;
import com.pm.patient_service.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public List<PatientResponseDTO> getPatient(){
        List<Patient> patients = patientRepository.findAll();
        return patients.stream().map(patientMapper::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("A patient with this email already exists" + patientRequestDTO.getEmail());
        }
        Patient newPatient = patientRepository.save(
                patientMapper.toModel(patientRequestDTO));

        billingServiceGrpcClient.createbillingAccount(newPatient.getId().toString()
                ,newPatient.getName(), newPatient.getEmail());

        kafkaProducer.sendEvent(newPatient);

        return patientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id,
                                            PatientRequestDTO patientRequestDTO)
    {
        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)){
            throw new EmailAlreadyExistsException("A patient with this email already exists" + patientRequestDTO.getEmail());
        }

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());

        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        return patientMapper.toDTO(patientRepository.save(patient));
    }

    public PatientResponseDTO deletePatient(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));
        patientRepository.delete(patient);
        return patientMapper.toDTO(patient);
    }
}
