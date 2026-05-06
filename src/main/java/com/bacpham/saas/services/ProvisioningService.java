package com.bacpham.saas.services;


import com.bacpham.saas.entities.Tenant;

public interface ProvisioningService {

    void provisionTenant(final Tenant tenant);
}