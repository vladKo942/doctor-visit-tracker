package doctorVisitTracker.dto;

public record VisitRequest(String start, String end, Long patientId, Long doctorId) {
}
