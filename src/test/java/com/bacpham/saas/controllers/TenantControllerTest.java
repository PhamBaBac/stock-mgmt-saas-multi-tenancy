package com.bacpham.saas.controllers;

import com.bacpham.saas.services.TenantService;
import com.bacpham.saas.security.JwtTokenService;
import com.bacpham.saas.config.TenantSchemaResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.context.annotation.Import;
import com.bacpham.saas.security.SecurityConfig;

@WebMvcTest(TenantController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TenantService tenantService;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    @MockitoBean
    private TenantSchemaResolver tenantSchemaResolver;

    @Test
    void approveTenant_shouldCallServiceAndReturnOk() throws Exception {
        String tenantId = "123";

        mockMvc.perform(post("/api/v1/tenants/approve/{tenant-id}", tenantId)
                        .with(csrf())
                        .with(user("admin").roles("PLATFORM_ADMIN")))
                .andExpect(status().isOk());

        verify(tenantService).approveTenant(tenantId);
    }

    @Test
    void activateTenant_shouldCallServiceAndReturnOk() throws Exception {
        String tenantId = "123";

        mockMvc.perform(patch("/api/v1/tenants/activate/{tenant-id}", tenantId)
                        .with(csrf())
                        .with(user("admin").roles("PLATFORM_ADMIN")))
                .andExpect(status().isOk());

        verify(tenantService).activateTenant(tenantId);
    }

    @Test
    void activateTenant_withInsufficientRole_shouldReturnForbidden() throws Exception {
        String tenantId = "123";

        mockMvc.perform(patch("/api/v1/tenants/activate/{tenant-id}", tenantId)
                        .with(csrf())
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }
}
