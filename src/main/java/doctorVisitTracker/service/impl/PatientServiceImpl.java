package doctorVisitTracker.service.impl;

import doctorVisitTracker.dto.PatientListResponse;
import doctorVisitTracker.dto.PatientResponse;
import doctorVisitTracker.dto.PatientVisitResponse;
import doctorVisitTracker.entity.Patient;
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

        Page<Patient> patientEntities = findPatients(search, doctorIds, pageable);

        List<PatientResponse> patients = patientEntities.getContent().stream()
                .map(this::mapToDto)
                .toList();

        return new PatientListResponse(patients, (int) patientEntities.getTotalElements());
    }

    private Page<Patient> findPatients(String search, List<Long> doctorIds, Pageable pageable) {
        if (search != null && hasDoctorIds(doctorIds)) {
            return patientRepository.findByFirstNameContainingIgnoreCaseAndDoctorIds(search, doctorIds, pageable);
        }
        if (search != null) {
            return patientRepository.findByFirstNameContainingIgnoreCase(search, pageable);
        }
        if (hasDoctorIds(doctorIds)) {
            return patientRepository.findByDoctorIds(doctorIds, pageable);
        }
        return patientRepository.findAll(pageable);
    }

    private boolean hasDoctorIds(List<Long> doctorIds) {
        return doctorIds != null && !doctorIds.isEmpty();
    }

    private PatientResponse mapToDto(Patient patient) {
        List<PatientVisitResponse> visitResponses = visitRepository.findLatestVisitsGroupedByDoctor(patient.getId())
                .stream()
                .map(visit -> PatientVisitResponse.from(visit, countTotalPatients(visit.getDoctor().getId())))
                .toList();

        return new PatientResponse(patient.getFirstName(), patient.getLastName(), visitResponses);
    }

    private int countTotalPatients(Long doctorId) {
        return visitRepository.countDistinctPatientsByDoctorId(doctorId);
    }
}
