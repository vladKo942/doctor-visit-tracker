package doctorVisitTracker.repository;

import doctorVisitTracker.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Visit v " +
            "WHERE v.doctor.id = :doctorId " +
            "AND ((v.startDateTime < :endDateTime AND v.endDateTime > :startDateTime))")
    boolean existsOverlappingVisit(Long doctorId, Instant startDateTime, Instant endDateTime);

}
