package edu.ysu.officeque.web;

import java.security.Principal;
import edu.ysu.officeque.service.QueueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/queue")
public class QueueController {
    private final QueueService service;

    public QueueController(QueueService service) {
        this.service = service;
    }

    public record JoinRequest(Long facultyId, Long sessionId, String reason) { }

    @PostMapping
    public ResponseEntity<QueueService.QueueReceipt> join(@RequestBody JoinRequest request, Principal principal) {
        if (request.facultyId() == null || request.sessionId() == null) {
            throw new IllegalArgumentException("Faculty and office-hour session are required");
        }
        var receipt = service.join(DemoIdentity.studentId(principal.getName()),
                request.facultyId(), request.sessionId(), request.reason());
        return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
    }
}
