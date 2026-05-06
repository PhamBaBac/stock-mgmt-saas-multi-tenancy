package com.bacpham.saas.services;

import com.bacpham.saas.common.PageResponse;
import com.bacpham.saas.requests.RegisterTenantRequest;
import com.bacpham.saas.responses.TenantResponse;

public interface TenantService {

    void registerTenant(final RegisterTenantRequest request);

    void approveTenant(final String tenantId);

    void activateTenant(final String tenantId);

    void deactivateTenant(final String tenantId);

    void suspendTenant(final String tenantId);

    PageResponse<TenantResponse> findAll(final int page, final int size);
}