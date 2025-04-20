package com.pm.patient_service.kafka;

import com.pm.patient_service.model.Patient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void sendEvent(Patient patient){
        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setEmail(patient.getEmail())
                .setName(patient.getName())
                .setEventType("PATIENT_CREATED")
                .build();

        try{
            kafkaTemplate.send("patient", event.toByteArray());
        }
        catch (Exception e){
            log.error("Error sending PatientCreated event {}", event);
        }
    }
}
