package doctorVisitTracker.service.impl;

import doctorVisitTracker.dto.VisitRequest;
import doctorVisitTracker.dto.VisitResponse;
import doctorVisitTracker.entity.Doctor;
import doctorVisitTracker.entity.Patient;
import doctorVisitTracker.entity.Visit;
import doctorVisitTracker.repository.DoctorRepository;
import doctorVisitTracker.repository.PatientRepository;
import doctorVisitTracker.repository.VisitRepository;
import doctorVisitTracker.service.VisitService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public VisitResponse createVisit(VisitRequest dto) {
        Doctor doctor = getDoctor(dto.doctorId());
        Patient patient = getPatient(dto.patientId());

        Instant start = parseAndConvertToUTC(dto.start());
        Instant end = parseAndConvertToUTC(dto.end());

        validateVisitAvailability(doctor.getId(), start, end);

        Visit visit = saveVisit(doctor, patient, start, end);

        return VisitResponse.from(visit);
    }

    public Instant parseAndConvertToUTC(String dateTime) {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toInstant();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Некоректний формат дати: " + dateTime +
                    ". Використовуйте формат yyyy-MM-dd'T'HH:mm:ssXXX.");
        }
    }

    private Doctor getDoctor(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Лікаря не знайдено"));
    }

    private Patient getPatient(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Пацієнта не знайдено"));
    }

    private void validateVisitAvailability(Long doctorId, Instant start, Instant end) {
        if (visitRepository.existsOverlappingVisit(doctorId, start, end)) {
            throw new IllegalArgumentException("Вказаний час вже зайнятий");
        }
    }

    private Visit saveVisit(Doctor doctor, Patient patient, Instant start, Instant end) {
        Visit visit = new Visit();
        visit.setDoctor(doctor);
        visit.setPatient(patient);
        visit.setStartDateTime(start);
        visit.setEndDateTime(end);
        return visitRepository.save(visit);
    }
}
