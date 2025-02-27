package doctorVisitTracker.repository;

import doctorVisitTracker.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Visit v " +
            "WHERE v.doctor.id = :doctorId " +
            "AND ((v.startDateTime < :endDateTime AND v.endDateTime > :startDateTime))")
    boolean existsOverlappingVisit(Long doctorId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("SELECT v FROM Visit v " +
            "WHERE v.patient.id = :patientId " +
            "AND v.startDateTime = (SELECT MAX(v2.startDateTime) FROM Visit v2 " +
            "WHERE v2.patient.id = v.patient.id AND v2.doctor.id = v.doctor.id)")
    List<Visit> findLatestVisitsGroupedByDoctor(Long patientId);

    @Query("SELECT COUNT(DISTINCT v.patient.id) FROM Visit v WHERE v.doctor.id = :doctorId")
    int countDistinctPatientsByDoctorId(Long doctorId);
}
