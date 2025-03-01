package doctorVisitTracker.dto;

import doctorVisitTracker.entity.Doctor;
import doctorVisitTracker.entity.Visit;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VisitResponseTest {

    @Test
    void shouldConvertVisitTimeToDoctorTimezone() {
        Doctor doctor = new Doctor();
        doctor.setTimezone("Europe/Kiev");

        Visit visit = new Visit();
        visit.setDoctor(doctor);
        visit.setStartDateTime(Instant.parse("2025-03-01T08:00:00Z"));
        visit.setEndDateTime(Instant.parse("2025-03-01T09:00:00Z"));

        VisitResponse response = VisitResponse.from(visit);

        assertEquals("2025-03-01T10:00:00+02:00", response.start());
        assertEquals("2025-03-01T11:00:00+02:00", response.end());
    }
}
