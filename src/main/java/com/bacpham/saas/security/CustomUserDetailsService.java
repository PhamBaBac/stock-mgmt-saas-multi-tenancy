package com.bacpham.saas.security;

import com.bacpham.saas.entities.Tenant;
import com.bacpham.saas.entities.User;
import com.bacpham.saas.entities.TenantStatus;
import com.bacpham.saas.exceptions.UnauthorizedException;
import com.bacpham.saas.repositories.TenantRepository;
import com.bacpham.saas.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        // First check if this is an admin account of a pending/suspended/inactive tenant
        final Optional<Tenant> optionalTenant = this.tenantRepository.findByAdminUsername(username);
        if (optionalTenant.isPresent()) {
            final TenantStatus status = optionalTenant.get().getStatus();
            if (status != TenantStatus.ACTIVE) {
                if (status == TenantStatus.PENDING) {
                    throw new UnauthorizedException("Your company account is pending approval. Please wait for the administrator to approve.");
                } else if (status == TenantStatus.SUSPENDED) {
                    throw new UnauthorizedException("Your company account has been suspended. Please contact the platform administrator.");
                } else if (status == TenantStatus.INACTIVE) {
                    throw new UnauthorizedException("Your company account is inactive. Please contact the platform administrator.");
                }
            }
        }

        final User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (user.getTenant() != null) {
            final TenantStatus status = user.getTenant().getStatus();
            if (status != TenantStatus.ACTIVE) {
                if (status == TenantStatus.SUSPENDED) {
                    throw new UnauthorizedException("Your company account has been suspended. Please contact the platform administrator.");
                } else if (status == TenantStatus.PENDING) {
                    throw new UnauthorizedException("Your company account is pending approval. Please wait for the administrator to approve.");
                } else if (status == TenantStatus.INACTIVE) {
                    throw new UnauthorizedException("Your company account is inactive. Please contact the platform administrator.");
                }
            }
        }

        return user;
    }
}


