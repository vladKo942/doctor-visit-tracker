package doctorVisitTracker.service;

import doctorVisitTracker.dto.VisitRequest;
import doctorVisitTracker.dto.VisitResponse;
import doctorVisitTracker.entity.Doctor;
import doctorVisitTracker.entity.Patient;
import doctorVisitTracker.entity.Visit;
import doctorVisitTracker.repository.DoctorRepository;
import doctorVisitTracker.repository.PatientRepository;
import doctorVisitTracker.repository.VisitRepository;
import doctorVisitTracker.service.impl.VisitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private VisitServiceImpl visitService;

    private Doctor doctor;
    private Patient patient;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("Олександр");
        doctor.setLastName("Федик");
        doctor.setTimezone("Europe/Kiev");

        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Іван");
        patient.setLastName("Петренко");
    }

    @Test
    void shouldCreateVisitSuccessfully() {
        VisitRequest request = new VisitRequest(
                "2025-03-01T10:00:00+02:00",
                "2025-03-01T10:30:00+02:00",
                1L,
                1L
        );

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(visitRepository.existsOverlappingVisit(anyLong(), any(), any())).thenReturn(false);

        Visit savedVisit = new Visit();
        savedVisit.setDoctor(doctor);
        savedVisit.setPatient(patient);
        savedVisit.setStartDateTime(LocalDateTime.of(2025, 3, 1, 10, 0));
        savedVisit.setEndDateTime(LocalDateTime.of(2025, 3, 1, 10, 30));

        when(visitRepository.save(any(Visit.class))).thenReturn(savedVisit);

        VisitResponse response = visitService.createVisit(request);

        assertNotNull(response);
        assertEquals("2025-03-01T10:00:00+02:00", response.start());
        assertEquals("2025-03-01T10:30:00+02:00", response.end());
        assertEquals(1L, response.doctorId());
    }

    @Test
    void shouldThrowExceptionWhenTimeSlotIsTaken() {
        VisitRequest request = new VisitRequest(
                "2025-03-01T10:00:00+02:00",
                "2025-03-01T10:30:00+02:00",
                1L,
                1L
        );

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(visitRepository.existsOverlappingVisit(anyLong(), any(), any())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                visitService.createVisit(request));

        assertEquals("Вказаний час вже зайнятий", exception.getMessage());
    }
}

