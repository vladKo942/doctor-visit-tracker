package doctorVisitTracker.service;

import doctorVisitTracker.dto.PatientListResponse;
import doctorVisitTracker.dto.PatientResponse;
import doctorVisitTracker.dto.PatientVisitResponse;
import doctorVisitTracker.entity.Doctor;
import doctorVisitTracker.entity.Patient;
import doctorVisitTracker.entity.Visit;
import doctorVisitTracker.repository.PatientRepository;
import doctorVisitTracker.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

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
        visit.setId(100L);
        visit.setDoctor(doctor);
        visit.setPatient(patient);
        visit.setStartDateTime(Instant.parse("2025-03-01T08:00:00Z"));
        visit.setEndDateTime(Instant.parse("2025-03-01T08:30:00Z"));
    }

    @Test
    void shouldReturnPatientsWithLastVisits() {
        List<Object[]> mockData = new ArrayList<>();
        mockData.add(new Object[]{patient, visit, 10});

        Page<Object[]> pageMock = new PageImpl<>(mockData, PageRequest.of(0, 10), mockData.size());

        when(patientRepository.findPatientsWithVisitsAndDoctorStats(any(), any(), any(Pageable.class)))
                .thenReturn(pageMock);

        PatientListResponse response = patientService.getPatients(0, 10, null, null);

        assertNotNull(response);
        assertEquals(1, response.count());
        assertEquals(1, response.data().size());

        PatientResponse patientResponse = response.data().getFirst();
        assertEquals("Іван", patientResponse.firstName());
        assertEquals("Петренко", patientResponse.lastName());

        assertEquals(1, patientResponse.lastVisits().size());
        PatientVisitResponse visitResponse = patientResponse.lastVisits().getFirst();

        assertEquals(10, visitResponse.doctor().totalPatients());
    }
}
