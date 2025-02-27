package doctorVisitTracker.repository;

import doctorVisitTracker.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Page<Patient> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Patient p " +
            "JOIN Visit v ON p.id = v.patient.id " +
            "WHERE v.doctor.id IN :doctorIds")
    Page<Patient> findByDoctorIds(List<Long> doctorIds, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Patient p " +
            "JOIN Visit v ON p.id = v.patient.id " +
            "WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "AND v.doctor.id IN :doctorIds")
    Page<Patient> findByFirstNameContainingIgnoreCaseAndDoctorIds(String search, List<Long> doctorIds, Pageable pageable);
}
