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
                .content("{\"name\":\"  CI Employee  \",\"department\":\" IT \",\"email\":\"CI.Employee@Example.Test\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("CI Employee"))
            .andExpect(jsonPath("$.email").value("ci.employee@example.test"))
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
            .andExpect(jsonPath("$.department").value("Platform"))
            .andExpect(jsonPath("$.email").value("updated@example.test"));

        mvc.perform(delete("/api/employees/{id}", id))
            .andExpect(status().isNoContent());

        mvc.perform(get("/api/employees/{id}", id))
            .andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidEmployeeFieldsOnCreate() throws Exception {
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"  \",\"department\":\"IT\",\"email\":\"a@example.test\"}"))
            .andExpect(status().isBadRequest());

        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Missing Email\",\"department\":\"IT\"}"))
            .andExpect(status().isBadRequest());

        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Bad Email\",\"department\":\"IT\",\"email\":\"not-an-email\"}"))
            .andExpect(status().isBadRequest());

        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Name Too Long " + "x".repeat(101) + "\",\"department\":\"IT\",\"email\":\"a@example.test\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsInvalidUpdateAndMissingEmployee() throws Exception {
        String created = mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Valid Employee\",\"department\":\"IT\",\"email\":\"valid@example.test\"}"))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(created).get("id").asLong();

        mvc.perform(put("/api/employees/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated\",\"department\":\"IT\",\"email\":\"invalid\"}"))
            .andExpect(status().isBadRequest());

        mvc.perform(put("/api/employees/{id}", 99999).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Missing\",\"department\":\"IT\",\"email\":\"missing@example.test\"}"))
            .andExpect(status().isNotFound());
    }
}
