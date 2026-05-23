package com.bacpham.saas.security;

import com.bacpham.saas.entities.Tenant;
import com.bacpham.saas.entities.TenantStatus;
import com.bacpham.saas.entities.User;
import com.bacpham.saas.entities.UserRole;
import com.bacpham.saas.exceptions.UnauthorizedException;
import com.bacpham.saas.repositories.TenantRepository;
import com.bacpham.saas.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Tenant pendingTenant;
    private Tenant activeTenant;
    private Tenant suspendedTenant;

    @BeforeEach
    void setUp() {
        pendingTenant = Tenant.builder().id("tenant-pending").status(TenantStatus.PENDING).build();
        activeTenant = Tenant.builder().id("tenant-active").status(TenantStatus.ACTIVE).build();
        suspendedTenant = Tenant.builder().id("tenant-suspended").status(TenantStatus.SUSPENDED).build();
    }

    @Test
    void loadUserByUsername_whenUserDoesNotExistAndTenantDoesNotExist_shouldThrowUsernameNotFoundException() {
        // Arrange
        String username = "nonexistent";
        when(tenantRepository.findByAdminUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });
        
        verify(tenantRepository).findByAdminUsername(username);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_whenTenantIsPending_shouldThrowUnauthorizedException() {
        // Arrange
        String username = "admin_pending";
        when(tenantRepository.findByAdminUsername(username)).thenReturn(Optional.of(pendingTenant));

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });
        
        assertTrue(exception.getMessage().contains("pending approval"));
        verify(tenantRepository).findByAdminUsername(username);
        verifyNoInteractions(userRepository);
    }

    @Test
    void loadUserByUsername_whenTenantIsSuspended_shouldThrowUnauthorizedException() {
        // Arrange
        String username = "admin_suspended";
        when(tenantRepository.findByAdminUsername(username)).thenReturn(Optional.of(suspendedTenant));

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });

        assertTrue(exception.getMessage().contains("suspended"));
        verify(tenantRepository).findByAdminUsername(username);
        verifyNoInteractions(userRepository);
    }

    @Test
    void loadUserByUsername_whenTenantIsActiveAndUserExists_shouldReturnUserDetails() {
        // Arrange
        String username = "admin_active";
        User mockUser = User.builder()
                .username(username)
                .tenant(activeTenant)
                .role(UserRole.ROLE_COMPANY_ADMIN)
                .enabled(true)
                .build();

        when(tenantRepository.findByAdminUsername(username)).thenReturn(Optional.of(activeTenant));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        verify(tenantRepository).findByAdminUsername(username);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_whenUserTenantIsSuspended_shouldThrowUnauthorizedException() {
        // Arrange
        String username = "user_suspended";
        User mockUser = User.builder()
                .username(username)
                .tenant(suspendedTenant)
                .role(UserRole.ROLE_USER)
                .enabled(true)
                .build();

        when(tenantRepository.findByAdminUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });

        assertTrue(exception.getMessage().contains("suspended"));
        verify(tenantRepository).findByAdminUsername(username);
        verify(userRepository).findByUsername(username);
    }
}
