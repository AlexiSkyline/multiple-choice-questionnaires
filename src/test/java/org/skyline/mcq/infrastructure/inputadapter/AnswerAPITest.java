package org.skyline.mcq.infrastructure.inputadapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyline.mcq.domain.models.Result;
import org.skyline.mcq.infrastructure.outputport.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AnswerAPITest {

    private static final String ANSWER_RESULT_ID_PATH = "/api/v1/answers/{resultId}";

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private Result resultTest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        resultTest = Result.builder()
                .startTime(Timestamp.valueOf(LocalDateTime.now()))
                .endTime(Timestamp.valueOf(LocalDateTime.now().plusMinutes(30)))
                .duration(1800)
                .totalPoints(20)
                .correctAnswers(10)
                .incorrectAnswers(10)
                .build();
    }

    @Test
    @Rollback
    @Transactional
    @DisplayName("List answers by result id: Should return 200 OK")
    void listAnswerByResultId() throws Exception {

        var result = resultRepository.save(resultTest);

        mockMvc.perform(get(ANSWER_RESULT_ID_PATH, result.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("List answers by result id: Should return 404 Not Found")
    void listAnswerByResultIdNotFound() throws Exception {

        mockMvc.perform(get(ANSWER_RESULT_ID_PATH, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}