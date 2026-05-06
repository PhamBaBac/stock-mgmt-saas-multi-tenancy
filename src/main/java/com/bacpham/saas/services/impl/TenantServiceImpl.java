package com.bacpham.saas.services.impl;

import com.bacpham.saas.common.PageResponse;
import com.bacpham.saas.entities.Tenant;
import com.bacpham.saas.entities.TenantStatus;
import com.bacpham.saas.exceptions.DuplicateResourceException;
import com.bacpham.saas.mappers.TenantMapper;
import com.bacpham.saas.repositories.TenantRepository;
import com.bacpham.saas.requests.RegisterTenantRequest;
import com.bacpham.saas.responses.TenantResponse;
import com.bacpham.saas.services.ProvisioningService;
import com.bacpham.saas.services.TenantService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantServiceImpl implements TenantService {
    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final PasswordEncoder passwordEncoder;
    private final ProvisioningService provisioningService;

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
    }

    @Override
    public void approveTenant(String tenantId) {
    }

    @Override
    public void activateTenant(String tenantId) {

    }

    @Override
    public void deactivateTenant(String tenantId) {

    }

    @Override
    public void suspendTenant(String tenantId) {

    }

    @Override
    public PageResponse<TenantResponse> findAll(int page, int size) {
        return null;
    }
}
