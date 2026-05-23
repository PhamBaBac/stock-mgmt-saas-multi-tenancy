package com.bacpham.saas.repositories;

import com.bacpham.saas.entities.Tenant;
import com.bacpham.saas.entities.TenantStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TenantRepositoryTest {

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void findByAdminUsername_whenAdminUsernameExists_shouldReturnTenant() {
        // Arrange
        Tenant tenant = Tenant.builder()
                .companyName("Test Company")
                .companyCode("TC")
                .email("tc@test.com")
                .adminFullName("Test Admin")
                .adminEmail("admin@test.com")
                .adminUsername("admin_username_test")
                .adminPassword("password")
                .status(TenantStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();

        tenantRepository.save(tenant);

        // Act
        Optional<Tenant> found = tenantRepository.findByAdminUsername("admin_username_test");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Test Company", found.get().getCompanyName());
    }
}
