package doctorVisitTracker.service;

import doctorVisitTracker.dto.VisitRequest;
import doctorVisitTracker.dto.VisitResponse;

public interface VisitService {

    VisitResponse createVisit(VisitRequest visitRequest);

}
