package doctorVisitTracker.service;

import doctorVisitTracker.dto.PatientListResponse;
import doctorVisitTracker.entity.Doctor;
import doctorVisitTracker.entity.Patient;
import doctorVisitTracker.entity.Visit;
import doctorVisitTracker.repository.PatientRepository;
import doctorVisitTracker.repository.VisitRepository;
import doctorVisitTracker.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient;
    private Doctor doctor;
    private Visit visit;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Іван");
        patient.setLastName("Петренко");

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("Олександр");
        doctor.setLastName("Федик");
        doctor.setTimezone("Europe/Kiev");

        visit = new Visit();
        visit.setDoctor(doctor);
        visit.setPatient(patient);
        visit.setStartDateTime(LocalDateTime.of(2025, 3, 1, 10, 0));
        visit.setEndDateTime(LocalDateTime.of(2025, 3, 1, 10, 30));
    }

    @Test
    void shouldReturnPatientsWithLastVisits() {
        when(patientRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(patient)));

        when(visitRepository.findLatestVisitsGroupedByDoctor(patient.getId()))
                .thenReturn(List.of(visit));

        when(visitRepository.countDistinctPatientsByDoctorId(doctor.getId()))
                .thenReturn(10);

        PatientListResponse response = patientService.getPatients(0, 10, null, null);

        assertNotNull(response);
        assertEquals(1, response.count());
        assertEquals(1, response.data().size());
        assertEquals("Іван", response.data().getFirst().firstName());
        assertEquals(1, response.data().getFirst().lastVisits().size());
        assertEquals("Олександр", response.data().getFirst().lastVisits().getFirst().doctor().firstName());
    }
}

