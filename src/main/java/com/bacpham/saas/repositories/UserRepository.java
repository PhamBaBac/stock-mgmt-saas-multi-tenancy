package com.bacpham.saas.repositories;


import com.bacpham.saas.entities.User;
import com.bacpham.saas.entities.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.deleted = false")
    Optional<User> findByIdAndNotDeleted(String id);

    Optional<User> findByUsername(String username);

    List<User> findByRole(UserRole role);

    boolean existsByUsername(String adminUsername);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.tenant.id = :tenantId AND u.deleted = false")
    Page<User> findAllByTenantId(String tenantId, Pageable pageable);
}
