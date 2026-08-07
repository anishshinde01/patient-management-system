package com.anishshinde.patientservice.kafka;

import com.anishshinde.patientservice.model.Patient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Slf4j // generate SLF4J logger
@Service
@RequiredArgsConstructor // autowire
public class KafkaProducer {

    // To publish messages to Kafka topics.
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void sendEvent(Patient patient) {
        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType("PATIENT_CREATED")
                .build();

        log.info("Publishing patient event to Kafka: {}", event);

        // Serialize the Protobuf event and publish it to the "patient" Kafka topic.
        kafkaTemplate.send("patient", event.toByteArray())
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send patient event.", ex);
                        return;
                    }

                    log.info(
                            "Patient event sent successfully. Topic: {}, Partition: {}, Offset: {}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset()
                    );
                });
    }

}