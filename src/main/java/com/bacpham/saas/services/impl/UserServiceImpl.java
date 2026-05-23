package com.bacpham.saas.services.impl;

import com.bacpham.saas.common.PageResponse;
import com.bacpham.saas.config.TenantContext;
import com.bacpham.saas.entities.Tenant;
import com.bacpham.saas.entities.User;
import com.bacpham.saas.entities.UserRole;
import com.bacpham.saas.exceptions.DuplicateResourceException;
import com.bacpham.saas.exceptions.InvalidRequestException;
import com.bacpham.saas.mappers.UserMapper;
import com.bacpham.saas.repositories.TenantRepository;
import com.bacpham.saas.repositories.UserRepository;
import com.bacpham.saas.requests.UserRequest;
import com.bacpham.saas.responses.UserResponse;
import com.bacpham.saas.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final TenantRepository tenantRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(final UserRequest request) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Creating user for tenant: {}", tenantId);

        // validate if username exists
        if (this.repository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        // check if email exists
        if (this.repository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // validate role (cannot be PLATFORM_ADMIN)
        if (request.getRole() == UserRole.ROLE_PLATFORM_ADMIN) {
            throw new InvalidRequestException("Role is required");
        }

        final User user = this.userMapper.toEntity(request);
        user.setPassword(this.passwordEncoder.encode(request.getPassword()));
        user.setTenant(Tenant.builder().id(tenantId).build());
        user.setEnabled(true);

        this.repository.save(user);

        log.info("User created successfully");
    }

    @Override
    public void updateUser(final String userId, final UserRequest request) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Updating user for tenant: {}", tenantId);

        final User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        // check if user belongs to the tenant
        if (!user.getTenant().getId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        // check if username is being changed and if it is already taken
        if (!user.getUsername().equals(request.getUsername()) && this.repository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        // check if email is being changed and if it is already taken
        if (!user.getEmail().equals(request.getEmail()) && this.repository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // validate role (cannot be PLATFORM_ADMIN)
        if (request.getRole() == UserRole.ROLE_PLATFORM_ADMIN) {
            throw new InvalidRequestException("Role is required");
        }

        // update user details
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        this.repository.save(user);
        log.info("User updated successfully");
    }

    @Override
    public void deleteUser(final String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        log.info("Deleting user for tenant: {}", tenantId);

        final User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        // check if user belongs to the tenant
        if (!user.getTenant().getId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        // soft delete user
        user.setDeleted(true);
        this.repository.save(user);
        log.info("User deleted successfully");
    }

    @Override
    public UserResponse getUserById(final String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        final User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        // check if user belongs to the tenant
        if (!user.getTenant().getId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }
        return this.userMapper.toResponse(user);
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(final int page, final int size) {
        final String tenantId = TenantContext.getCurrentTenant();
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<User> userPage = this.repository.findAllByTenantId(tenantId, pageRequest);
        final Page<UserResponse> userResponses = userPage.map(this.userMapper::toResponse);
        return PageResponse.of(userResponses);
    }

    @Override
    public void enableUser(final String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        final User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        // check if user belongs to the tenant
        if (!user.getTenant().getId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        user.setEnabled(true);
        this.repository.save(user);
        log.info("User enabled successfully");
    }

    @Override
    public void disableUser(final String userId) {
        final String tenantId = TenantContext.getCurrentTenant();
        final User user = this.repository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        // check if user belongs to the tenant
        if (!user.getTenant().getId().equals(tenantId)) {
            throw new InvalidRequestException("User does not belong to the tenant");
        }

        user.setEnabled(false);
        this.repository.save(user);
        log.info("User disabled successfully");
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

        return this.repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user was found with: " + username));
    }
}