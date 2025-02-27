package doctorVisitTracker.dto;

import doctorVisitTracker.entity.Patient;

import java.util.List;

public record PatientResponse(String firstName,
                              String lastName,
                              List<PatientVisitResponse> lastVisits) {

    public static PatientResponse from(Patient entity, List<PatientVisitResponse> visits) {
        return new PatientResponse(entity.getFirstName(), entity.getLastName(), visits);
    }
}
