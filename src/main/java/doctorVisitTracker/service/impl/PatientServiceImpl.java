package doctorVisitTracker.service.impl;

import doctorVisitTracker.dto.PatientListResponse;
import doctorVisitTracker.dto.PatientResponse;
import doctorVisitTracker.dto.PatientVisitResponse;
import doctorVisitTracker.entity.Patient;
import doctorVisitTracker.entity.Visit;
import doctorVisitTracker.repository.PatientRepository;
import doctorVisitTracker.repository.VisitRepository;
import doctorVisitTracker.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;

    @Override
    public PatientListResponse getPatients(int page, int size, String search, List<Long> doctorIds) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Patient> patientEntities;

        if (search != null && doctorIds != null && !doctorIds.isEmpty()) {
            patientEntities = patientRepository.findByFirstNameContainingIgnoreCaseAndDoctorIds(search, doctorIds, pageable);
        } else if (search != null) {
            patientEntities = patientRepository.findByFirstNameContainingIgnoreCase(search, pageable);
        } else if (doctorIds != null && !doctorIds.isEmpty()) {
            patientEntities = patientRepository.findByDoctorIds(doctorIds, pageable);
        } else {
            patientEntities = patientRepository.findAll(pageable);
        }

        List<PatientResponse> patients = patientEntities.getContent().stream()
                .map(this::mapToDto)
                .toList();

        return new PatientListResponse(patients, (int) patientEntities.getTotalElements());
    }

    private PatientResponse mapToDto(Patient patient) {
        List<Visit> lastVisitEntities = visitRepository.findLatestVisitsGroupedByDoctor(patient.getId());

        List<PatientVisitResponse> visitResponses = lastVisitEntities.stream()
                .map(visit -> {
                    int totalPatients = visitRepository.countDistinctPatientsByDoctorId(visit.getDoctor().getId());
                    return PatientVisitResponse.from(visit, totalPatients);
                })
                .toList();

        return new PatientResponse(patient.getFirstName(), patient.getLastName(), visitResponses);
    }
}
