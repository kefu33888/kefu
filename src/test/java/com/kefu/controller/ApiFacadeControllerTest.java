package com.kefu.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApiFacadeControllerTest {

    @Test
    void adminUserPermissionsEndpointShouldReturnPermissions() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ApiFacadeController()).build();

        mockMvc.perform(get("/api/admin/users/my-permissions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.isAdmin").value(true));
    }
}
