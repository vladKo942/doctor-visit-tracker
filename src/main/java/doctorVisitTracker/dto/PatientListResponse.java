package doctorVisitTracker.dto;

import java.util.List;

public record PatientListResponse(List<PatientResponse> data, int count) {
}
