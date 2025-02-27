package doctorVisitTracker.controller;

import doctorVisitTracker.dto.VisitRequest;
import doctorVisitTracker.dto.VisitResponse;
import doctorVisitTracker.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    public ResponseEntity<?> createVisit(@RequestBody VisitRequest visitRequest) {
        VisitResponse visitResponse = visitService.createVisit(visitRequest);
        return ResponseEntity.ok(visitResponse);
    }
}
