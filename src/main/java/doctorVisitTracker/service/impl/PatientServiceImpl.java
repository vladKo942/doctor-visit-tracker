package doctorVisitTracker.service.impl;

import doctorVisitTracker.dto.PatientListResponse;
import doctorVisitTracker.dto.PatientResponse;
import doctorVisitTracker.dto.PatientVisitResponse;
import doctorVisitTracker.entity.Patient;
import doctorVisitTracker.entity.Visit;
import doctorVisitTracker.repository.PatientRepository;
import doctorVisitTracker.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public PatientListResponse getPatients(int page, int size, String search, List<Long> doctorIds) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Object[]> resultPage = patientRepository.findPatientsWithVisitsAndDoctorStats(search, doctorIds, pageable);

        List<PatientResponse> patients = resultPage.getContent().stream().map(record -> {
            Patient patient = (Patient) record[0];
            Visit visit = (Visit) record[1];
            int patientCount = ((Number) record[2]).intValue();

            List<PatientVisitResponse> visitResponses = visit != null
                    ? List.of(PatientVisitResponse.from(visit, patientCount))
                    : Collections.emptyList();

            return new PatientResponse(patient.getFirstName(), patient.getLastName(), visitResponses);
        }).toList();

        return new PatientListResponse(patients, (int) resultPage.getTotalElements());
    }
}
