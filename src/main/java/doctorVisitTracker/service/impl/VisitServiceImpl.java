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

import java.time.ZoneId;
import java.time.ZonedDateTime;

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

        ZonedDateTime start = convertToDoctorTimezone(dto.start(), doctor.getTimezone());
        ZonedDateTime end = convertToDoctorTimezone(dto.end(), doctor.getTimezone());

        validateVisitAvailability(doctor.getId(), start, end);

        Visit visit = saveVisit(doctor, patient, start, end);

        return VisitResponse.from(visit);
    }

    private Doctor getDoctor(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Лікаря не знайдено"));
    }

    private Patient getPatient(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Пацієнта не знайдено"));
    }

    private ZonedDateTime convertToDoctorTimezone(String dateTime, String timezone) {
        return ZonedDateTime.parse(dateTime).withZoneSameInstant(ZoneId.of(timezone));
    }

    private void validateVisitAvailability(Long doctorId, ZonedDateTime start, ZonedDateTime end) {
        boolean exists = visitRepository.existsOverlappingVisit(doctorId, start.toLocalDateTime(), end.toLocalDateTime());
        if (exists) {
            throw new IllegalArgumentException("Вказаний час вже зайнятий");
        }
    }

    private Visit saveVisit(Doctor doctor, Patient patient, ZonedDateTime start, ZonedDateTime end) {
        Visit visit = new Visit();
        visit.setDoctor(doctor);
        visit.setPatient(patient);
        visit.setStartDateTime(start.toLocalDateTime());
        visit.setEndDateTime(end.toLocalDateTime());
        return visitRepository.save(visit);
    }
}
