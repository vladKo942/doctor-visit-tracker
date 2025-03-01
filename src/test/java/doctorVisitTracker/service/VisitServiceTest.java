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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
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
        String requestStart = "2025-03-01T10:00:00+02:00";
        String requestEnd = "2025-03-01T10:30:00+02:00";

        VisitRequest request = new VisitRequest(requestStart, requestEnd, 1L, 1L);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(visitRepository.existsOverlappingVisit(anyLong(), any(), any())).thenReturn(false);

        Instant expectedStartUtc = Instant.parse("2025-03-01T08:00:00Z");
        Instant expectedEndUtc = Instant.parse("2025-03-01T08:30:00Z");

        Visit savedVisit = new Visit();
        savedVisit.setDoctor(doctor);
        savedVisit.setPatient(patient);
        savedVisit.setStartDateTime(expectedStartUtc);
        savedVisit.setEndDateTime(expectedEndUtc);

        when(visitRepository.save(any(Visit.class))).thenReturn(savedVisit);

        VisitResponse response = visitService.createVisit(request);

        assertNotNull(response);
        assertEquals("2025-03-01T10:00:00+02:00", response.start());
        assertEquals("2025-03-01T10:30:00+02:00", response.end());
        assertEquals(1L, response.doctorId());

        ArgumentCaptor<Visit> visitCaptor = ArgumentCaptor.forClass(Visit.class);
        verify(visitRepository).save(visitCaptor.capture());

        Visit capturedVisit = visitCaptor.getValue();
        assertEquals(expectedStartUtc, capturedVisit.getStartDateTime());
        assertEquals(expectedEndUtc, capturedVisit.getEndDateTime());
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

    @Test
    void shouldSaveVisitInUTC() {
        Doctor doctor = new Doctor();
        doctor.setTimezone("Europe/Kiev");

        Patient patient = new Patient();

        Instant startUtc = Instant.parse("2025-03-01T08:00:00Z");
        Instant endUtc = Instant.parse("2025-03-01T09:00:00Z");

        Visit visit = new Visit();
        visit.setDoctor(doctor);
        visit.setPatient(patient);
        visit.setStartDateTime(startUtc);
        visit.setEndDateTime(endUtc);

        when(visitRepository.save(any(Visit.class))).thenAnswer(invocation -> {
            Visit saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Visit savedVisit = visitRepository.save(visit);

        assertNotNull(savedVisit.getId());
        assertEquals(startUtc, savedVisit.getStartDateTime());
        assertEquals(endUtc, savedVisit.getEndDateTime());
    }

    @Test
    void shouldThrowExceptionForInvalidDateFormat() {
        String invalidDate = "2025-03-01 10:00:00";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                visitService.parseAndConvertToUTC(invalidDate));

        assertEquals("Некоректний формат дати: 2025-03-01 10:00:00. Використовуйте формат yyyy-MM-dd'T'HH:mm:ssXXX.",
                exception.getMessage());
    }

}

