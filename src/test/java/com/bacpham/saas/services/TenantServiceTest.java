package com.bacpham.saas.services;

import com.bacpham.saas.entities.Tenant;
import com.bacpham.saas.entities.TenantStatus;
import com.bacpham.saas.exceptions.InvalidRequestException;
import com.bacpham.saas.repositories.TenantRepository;
import com.bacpham.saas.repositories.UserRepository;
import com.bacpham.saas.services.impl.TenantServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TenantServiceImpl tenantService;

    private Tenant pendingTenant;
    private Tenant activeTenant;
    private Tenant suspendedTenant;
    private Tenant inactiveTenant;

    @BeforeEach
    void setUp() {
        pendingTenant = Tenant.builder().id("1").companyName("Pending").status(TenantStatus.PENDING).build();
        activeTenant = Tenant.builder().id("2").companyName("Active").status(TenantStatus.ACTIVE).build();
        suspendedTenant = Tenant.builder().id("3").companyName("Suspended").status(TenantStatus.SUSPENDED).build();
        inactiveTenant = Tenant.builder().id("4").companyName("Inactive").status(TenantStatus.INACTIVE).build();
    }

    @Test
    void activateTenant_whenTenantDoesNotExist_shouldThrowEntityNotFoundException() {
        String id = "nonexistent";
        when(tenantRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tenantService.activateTenant(id));
        verify(tenantRepository).findById(id);
        verify(tenantRepository, never()).save(any());
    }

    @Test
    void activateTenant_whenTenantIsPending_shouldThrowInvalidRequestException() {
        String id = "1";
        when(tenantRepository.findById(id)).thenReturn(Optional.of(pendingTenant));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> tenantService.activateTenant(id));
        assertEquals("Tenant must be approved first", exception.getMessage());
        
        verify(tenantRepository).findById(id);
        verify(tenantRepository, never()).save(any());
    }

    @Test
    void activateTenant_whenTenantIsSuspended_shouldSuccessfullyActivate() {
        String id = "3";
        when(tenantRepository.findById(id)).thenReturn(Optional.of(suspendedTenant));

        tenantService.activateTenant(id);

        assertEquals(TenantStatus.ACTIVE, suspendedTenant.getStatus());
        verify(tenantRepository).findById(id);
        verify(tenantRepository).save(suspendedTenant);
    }

    @Test
    void deactivateTenant_whenTenantIsActive_shouldSuccessfullyDeactivate() {
        String id = "2";
        when(tenantRepository.findById(id)).thenReturn(Optional.of(activeTenant));

        tenantService.deactivateTenant(id);

        assertEquals(TenantStatus.INACTIVE, activeTenant.getStatus());
        verify(tenantRepository).findById(id);
        verify(tenantRepository).save(activeTenant);
    }

    @Test
    void deactivateTenant_whenTenantIsNotActive_shouldThrowInvalidRequestException() {
        String id = "3";
        when(tenantRepository.findById(id)).thenReturn(Optional.of(suspendedTenant));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> tenantService.deactivateTenant(id));
        assertEquals("Tenant must be active to be deactivated", exception.getMessage());

        verify(tenantRepository).findById(id);
        verify(tenantRepository, never()).save(any());
    }

    @Test
    void suspendTenant_whenTenantIsActive_shouldSuccessfullySuspend() {
        String id = "2";
        when(tenantRepository.findById(id)).thenReturn(Optional.of(activeTenant));

        tenantService.suspendTenant(id);

        assertEquals(TenantStatus.SUSPENDED, activeTenant.getStatus());
        verify(tenantRepository).findById(id);
        verify(tenantRepository).save(activeTenant);
    }
}
