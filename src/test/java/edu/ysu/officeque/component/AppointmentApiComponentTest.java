package edu.ysu.officeque.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.LocalDateTime;
import edu.ysu.officeque.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AppointmentApiComponentTest {
    @Autowired MockMvc mvc;
    @Autowired AppointmentRepository appointments;

    private final LocalDateTime start = LocalDateTime.now(Clock.systemUTC())
            .plusDays(7).withHour(14).withMinute(0).withSecond(0).withNano(0);

    private String request(LocalDateTime from, LocalDateTime to) {
        return """
                {"facultyId":1,"startTime":"%s","endTime":"%s"}
                """.formatted(from, to);
    }

    @Test
    void validRequestPersistsPendingAppointmentNotConfirmed() throws Exception {
        mvc.perform(post("/api/appointments").with(httpBasic("student1", "demo-only"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request(start, start.plusMinutes(30))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.message").value("Request submitted; awaiting faculty approval"));
        assertEquals(1, appointments.count());
    }

    @Test
    void overlappingSecondStudentsRequestReturns409AndOnlyOneRecord() throws Exception {
        mvc.perform(post("/api/appointments").with(httpBasic("student1", "demo-only"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request(start, start.plusMinutes(30))))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/appointments").with(httpBasic("student2", "demo-only"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request(start.plusMinutes(15), start.plusMinutes(45))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Requested appointment overlaps an existing booking"));
        assertEquals(1, appointments.count());
    }

    @Test
    void adjacentNonOverlappingSlotCanBeRequested() throws Exception {
        mvc.perform(post("/api/appointments").with(httpBasic("student1", "demo-only"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request(start, start.plusMinutes(30))))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/appointments").with(httpBasic("student2", "demo-only"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request(start.plusMinutes(30), start.plusMinutes(60))))
                .andExpect(status().isCreated());
        assertEquals(2, appointments.count());
    }

    @Test
    void invalidEndBeforeStartReturns400WithoutSaving() throws Exception {
        mvc.perform(post("/api/appointments").with(httpBasic("student1", "demo-only"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request(start, start.minusMinutes(5))))
                .andExpect(status().isBadRequest());
        assertEquals(0, appointments.count());
    }
}
