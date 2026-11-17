package edu.ysu.officeque.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.ysu.officeque.domain.OfficeHourSession;
import edu.ysu.officeque.repository.OfficeHourSessionRepository;
import edu.ysu.officeque.repository.QueueEntryRepository;
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
class QueueApiComponentTest {
    @Autowired MockMvc mvc;
    @Autowired OfficeHourSessionRepository sessions;
    @Autowired QueueEntryRepository entries;

    private String request(long sessionId) {
        return """
                {"facultyId":1,"sessionId":%d,"reason":"Help with Assignment 4"}
                """.formatted(sessionId);
    }

    @Test
    void validJoinPersistsWaitingEntryAndReturnsPosition() throws Exception {
        long sessionId = sessions.save(new OfficeHourSession(1L, true)).getId();
        mvc.perform(post("/api/queue").with(httpBasic("student1", "demo-only"))
                        .contentType(MediaType.APPLICATION_JSON).content(request(sessionId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.position").value(1))
                .andExpect(jsonPath("$.estimatedWaitMinutes").value(0));
        assertEquals(1, entries.count(), "Controller, service and repository must save one entry");
    }

    @Test
    void repeatedJoinReturnsConflictAndDoesNotDuplicateRecord() throws Exception {
        long sessionId = sessions.save(new OfficeHourSession(1L, true)).getId();
        mvc.perform(post("/api/queue").with(httpBasic("student1", "demo-only"))
                        .contentType(MediaType.APPLICATION_JSON).content(request(sessionId)))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/queue").with(httpBasic("student1", "demo-only"))
                        .contentType(MediaType.APPLICATION_JSON).content(request(sessionId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Student already has an active queue entry"));
        assertEquals(1, entries.count(), "A rejected duplicate cannot change persisted queue size");
    }

    @Test
    void secondStudentReceivesPositionTwoAndFourMinuteEstimate() throws Exception {
        long sessionId = sessions.save(new OfficeHourSession(1L, true)).getId();
        mvc.perform(post("/api/queue").with(httpBasic("student1", "demo-only"))
                        .contentType(MediaType.APPLICATION_JSON).content(request(sessionId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.position").value(1));
        mvc.perform(post("/api/queue").with(httpBasic("student2", "demo-only"))
                        .contentType(MediaType.APPLICATION_JSON).content(request(sessionId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.position").value(2))
                .andExpect(jsonPath("$.estimatedWaitMinutes").value(4));
        assertEquals(2, entries.count());
    }

    @Test
    void closedOfficeHoursRejectJoinAndDoNotSave() throws Exception {
        long sessionId = sessions.save(new OfficeHourSession(1L, false)).getId();
        mvc.perform(post("/api/queue").with(httpBasic("student1", "demo-only"))
                        .contentType(MediaType.APPLICATION_JSON).content(request(sessionId)))
                .andExpect(status().isConflict());
        assertEquals(0, entries.count());
    }

    @Test
    void facultyAccountCannotUseStudentOnlyQueueJoinEndpoint() throws Exception {
        long sessionId = sessions.save(new OfficeHourSession(1L, true)).getId();
        mvc.perform(post("/api/queue").with(httpBasic("faculty1", "demo-only"))
                        .contentType(MediaType.APPLICATION_JSON).content(request(sessionId)))
                .andExpect(status().isForbidden());
        assertEquals(0, entries.count());
    }
}
