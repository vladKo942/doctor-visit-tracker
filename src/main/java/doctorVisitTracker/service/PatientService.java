package doctorVisitTracker.service;

import doctorVisitTracker.dto.PatientListResponse;

import java.util.List;

public interface PatientService {

    PatientListResponse getPatients(int page, int size, String search, List<Long> doctorIds);

}
