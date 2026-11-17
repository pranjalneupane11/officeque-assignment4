package edu.ysu.officeque.web;

import java.security.Principal;
import java.time.LocalDateTime;
import edu.ysu.officeque.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    public record AppointmentRequest(Long facultyId, LocalDateTime startTime, LocalDateTime endTime) { }

    @PostMapping
    public ResponseEntity<AppointmentService.AppointmentReceipt> request(
            @RequestBody AppointmentRequest request, Principal principal) {
        if (request.facultyId() == null) {
            throw new IllegalArgumentException("Faculty ID is required");
        }
        var receipt = service.request(DemoIdentity.studentId(principal.getName()),
                request.facultyId(), request.startTime(), request.endTime());
        return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
    }
}
