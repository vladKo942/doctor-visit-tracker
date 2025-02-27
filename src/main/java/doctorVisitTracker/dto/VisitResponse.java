package doctorVisitTracker.dto;

import doctorVisitTracker.entity.Visit;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record VisitResponse(String start, String end, Long doctorId) {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    public static VisitResponse from(Visit visit) {
        ZoneId zoneId = ZoneId.of(visit.getDoctor().getTimezone());

        return new VisitResponse(
                visit.getStartDateTime().atZone(zoneId).format(FORMATTER),
                visit.getEndDateTime().atZone(zoneId).format(FORMATTER),
                visit.getDoctor().getId()
        );
    }
}
