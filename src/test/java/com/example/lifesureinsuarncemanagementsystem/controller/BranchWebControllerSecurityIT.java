package com.example.lifesureinsuarncemanagementsystem.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BranchWebControllerSecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRenderBranchListForManager() throws Exception {
        mockMvc.perform(get("/branches").with(user("manager").authorities(() -> "MANAGER")))
                .andExpect(status().isOk())
                .andExpect(view().name("branches/list"));
    }

    @Test
    void shouldDenyAccessForEmployeeRole() throws Exception {
        mockMvc.perform(get("/branches").with(user("employee").authorities(() -> "EMPLOYEE")))
                .andExpect(status().isForbidden());
    }
}
