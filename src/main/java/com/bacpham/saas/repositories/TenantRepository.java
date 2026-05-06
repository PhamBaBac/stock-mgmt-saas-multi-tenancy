package com.bacpham.saas.repositories;

import com.bacpham.saas.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, String> {

    boolean existsByCompanyCode(String companyCode);

    boolean existsByEmail(String email);
}