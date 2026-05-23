package com.bacpham.saas.repositories;

import com.bacpham.saas.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, String> {

    boolean existsByCompanyCode(String companyCode);

    boolean existsByEmail(String email);

    Optional<Tenant> findByAdminUsername(String adminUsername);
}