package com.portfolio.employee;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired EmployeeRepository repository;
    @Autowired ObjectMapper mapper;

    @BeforeEach
    void cleanDatabase() { repository.deleteAll(); }

    @Test
    void createsListsReadsUpdatesAndDeletesEmployee() throws Exception {
        String created = mvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"CI Employee\",\"department\":\"IT\",\"email\":\"ci.employee@example.test\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("CI Employee"))
            .andReturn().getResponse().getContentAsString();

        JsonNode employee = mapper.readTree(created);
        long id = employee.get("id").asLong();

        mvc.perform(get("/api/employees"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].email").value("ci.employee@example.test"));

        mvc.perform(get("/api/employees/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.department").value("IT"));

        mvc.perform(put("/api/employees/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated CI Employee\",\"department\":\"Platform\",\"email\":\"updated@example.test\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.department").value("Platform"));

        mvc.perform(delete("/api/employees/{id}", id))
            .andExpect(status().isNoContent());

        mvc.perform(get("/api/employees/{id}", id))
            .andExpect(status().isNotFound());
    }

    @Test
    void rejectsMissingNameOrEmail() throws Exception {
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"  \",\"department\":\"IT\",\"email\":\"a@example.test\"}"))
            .andExpect(status().isBadRequest());

        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Missing Email\",\"department\":\"IT\"}"))
            .andExpect(status().isBadRequest());
    }
}
