package com.bacpham.saas.services.impl;

import com.bacpham.saas.common.PageResponse;
import com.bacpham.saas.entities.Tenant;
import com.bacpham.saas.entities.TenantStatus;
import com.bacpham.saas.entities.User;
import com.bacpham.saas.entities.UserRole;
import com.bacpham.saas.exceptions.DuplicateResourceException;
import com.bacpham.saas.exceptions.InvalidRequestException;
import com.bacpham.saas.mappers.TenantMapper;
import com.bacpham.saas.repositories.TenantRepository;
import com.bacpham.saas.repositories.UserRepository;
import com.bacpham.saas.requests.RegisterTenantRequest;
import com.bacpham.saas.responses.TenantResponse;
import com.bacpham.saas.services.ProvisioningService;
import com.bacpham.saas.services.TenantService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantServiceImpl implements TenantService {
    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ProvisioningService provisioningService;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Override
    public void registerTenant(RegisterTenantRequest request) {
        if (this.tenantRepository.existsByCompanyCode(request.getCompanyCode())) {
            throw new DuplicateResourceException("Tenant already exists");
        }

        // check if email already exits
        if (this.tenantRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Tenant Email already exists");
        }

        // create tenant entity
        final Tenant tenant = this.tenantMapper.toEntity(request);
        tenant.setAdminPassword(this.passwordEncoder.encode(request.getAdminPassword()));
        tenant.setStatus(TenantStatus.PENDING);

        this.tenantRepository.save(tenant);

        // Publish registration event
        this.eventPublisher.publishEvent(new com.bacpham.saas.events.TenantRegisteredEvent(
                this, tenant.getCompanyName(), tenant.getCompanyCode(), tenant.getAdminEmail()
        ));
        log.info("Published TenantRegisteredEvent for tenant: {}", tenant.getCompanyName());
    }

    @Override
    public void approveTenant(String tenantId) {
        // check if tenant exists
        final Tenant tenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));

        // activate tenant
        tenant.setStatus(TenantStatus.ACTIVE);
        this.tenantRepository.save(tenant);

        try {
            // provision the schema for the tenant
            this.provisioningService.provisionTenant(tenant);
            // create initial admin user
            createInitiaAdminUser(tenant);
        } catch (final Exception e) {
            rollbackTenantStatus(tenant);
        }
    }

    @Override
    public void activateTenant(String tenantId) {
        final Tenant tenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));

        if (tenant.getStatus() == TenantStatus.PENDING) {
            throw new InvalidRequestException("Tenant must be approved first");
        }

        tenant.setStatus(TenantStatus.ACTIVE);
        this.tenantRepository.save(tenant);
    }

    @Override
    public void deactivateTenant(String tenantId) {
        final Tenant tenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));

        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new InvalidRequestException("Tenant must be active to be deactivated");
        }

        tenant.setStatus(TenantStatus.INACTIVE);
        this.tenantRepository.save(tenant);
    }

    @Override
    public void suspendTenant(String tenantId) {
        final Tenant tenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));

        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new InvalidRequestException("Tenant must be active to be suspended");
        }

        tenant.setStatus(TenantStatus.SUSPENDED);
        this.tenantRepository.save(tenant);

    }

    @Override
    public PageResponse<TenantResponse> findAll(int page, int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<Tenant> tenants = this.tenantRepository.findAll(pageRequest);
        final Page<TenantResponse> tenantResponses = tenants.map(this.tenantMapper::toResponse);
        return PageResponse.of(tenantResponses);
    }

    private void rollbackTenantStatus(final Tenant tenant) {
        tenant.setStatus(TenantStatus.PENDING);
        this.tenantRepository.save(tenant);
    }

    private void createInitiaAdminUser(final Tenant tenant) {
        // check if the user already exists
        if (this.userRepository.existsByUsername(tenant.getAdminUsername())) {
            throw new DuplicateResourceException("User already exists");
        }

        final User adminUser = User.builder()
                .username(tenant.getAdminUsername())
                .email(tenant.getAdminEmail())
                .firstName(extractFirstName(tenant.getAdminFullName()))
                .lastName(extractLastName(tenant.getAdminFullName()))
                .password(tenant.getAdminPassword())
                .role(UserRole.ROLE_COMPANY_ADMIN)
                .tenant(tenant)
                .enabled(true)
                .build();
        this.userRepository.save(adminUser);
        log.info("Created initial admin user for tenant {}", tenant.getId());
    }

    private String extractFirstName(final String fullName) {
        return fullName.split(" ")[0];
    }

    private String extractLastName(final String fullName) {
        return fullName.split(" ").length > 1 ? fullName.split(" ")[1] : fullName;
    }
}
