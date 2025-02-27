package doctorVisitTracker.dto;

import doctorVisitTracker.entity.Visit;

public record VisitResponse(String start, String end, Long doctorId) {

    public static VisitResponse from(Visit visit) {
        return new VisitResponse(
                visit.getStartDateTime().toString(),
                visit.getEndDateTime().toString(),
                visit.getDoctor().getId()
        );
    }
}
