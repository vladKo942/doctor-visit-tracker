package doctorVisitTracker.repository;

import doctorVisitTracker.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("""
    SELECT p, v, COUNT(DISTINCT v2.patient.id)
    FROM Patient p
    LEFT JOIN Visit v ON v.startDateTime = (
        SELECT MAX(v2.startDateTime) FROM Visit v2 WHERE v2.patient.id = p.id
    )
    LEFT JOIN Visit v2 ON v2.doctor.id = v.doctor.id
    WHERE (:search IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')))
    AND (:doctorIds IS NULL OR v.doctor.id IN :doctorIds)
    GROUP BY p, v""")
    Page<Object[]> findPatientsWithVisitsAndDoctorStats(String search, List<Long> doctorIds, Pageable pageable);
}
