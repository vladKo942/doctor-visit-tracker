package doctorVisitTracker.dto;

import doctorVisitTracker.entity.Visit;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public record PatientVisitResponse(String start, String end, DoctorResponse doctor) {

    public static PatientVisitResponse from(Visit visit, int totalPatients) {
        ZonedDateTime start = visit.getStartDateTime().atZone(ZoneId.of(visit.getDoctor().getTimezone()));
        ZonedDateTime end = visit.getEndDateTime().atZone(ZoneId.of(visit.getDoctor().getTimezone()));

        return new PatientVisitResponse(
                start.toString(),
                end.toString(),
                new DoctorResponse(visit.getDoctor().getFirstName(), visit.getDoctor().getLastName(), totalPatients)
        );
    }
}
