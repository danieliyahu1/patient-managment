package com.pm.patient_service.mapper;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = "id", expression = "java(patient.getId().toString())")
    @Mapping(target = "dateOfBirth", expression = "java(patient.getDateOfBirth().toString())")
    PatientResponseDTO toDTO(Patient patient);


    // Convert String to LocalDate for dateOfBirth
    @Mapping(target = "dateOfBirth", expression = "java(dto.getDateOfBirth() != null ? java.time.LocalDate.parse(dto.getDateOfBirth()) : null)")
    Patient toModel(PatientRequestDTO dto);
}
