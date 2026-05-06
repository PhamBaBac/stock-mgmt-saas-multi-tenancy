# StackOverflowError Fix - Summary

## Problem
Your application was throwing a `StackOverflowError` when attempting to authenticate users at the `/api/v1/auth/login` endpoint. The error was:

```
java.lang.StackOverflowError
at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:215)
at jdk.proxy4/jdk.proxy4.$Proxy171.authenticate(Unknown Source)
```

## Root Causes

### 1. Missing UserDetailsService Implementation
The application was missing a `UserDetailsService` bean implementation. When the `AuthenticationManager` tried to authenticate a user, it had no provider configured to validate the username/password against the database.

### 2. Missing AuthenticationProvider Configuration
Without a proper `AuthenticationProvider` (specifically `DaoAuthenticationProvider`), Spring Security couldn't perform authentication. This caused the system to fall back to default mechanisms, which triggered infinite recursion through AOP proxies.

### 3. Unnecessary @EnableMethodSecurity
The `SecurityConfig` class had `@EnableMethodSecurity` annotation enabled, which creates AOP proxies for method-level security checks. Since your application wasn't using any `@PreAuthorize`, `@PostAuthorize`, or `@Secured` annotations, this created unnecessary proxy overhead and contributed to the proxy chain issues.

## Solution

### 1. Created CustomUserDetailsService
**File:** `src/main/java/com/bacpham/saas/security/CustomUserDetailsService.java`

This new service implements Spring Security's `UserDetailsService` interface and loads users from the database by username using the existing `UserRepository`.

```java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
```

### 2. Updated BeansConfigs
**File:** `src/main/java/com/bacpham/saas/config/BeansConfigs.java`

Added proper `AuthenticationProvider` bean configuration using `DaoAuthenticationProvider`, which:
- Takes the `CustomUserDetailsService` to load users
- Uses the `PasswordEncoder` to validate passwords against the stored hashes

```java
@Bean
public AuthenticationProvider authenticationProvider(
    final CustomUserDetailsService userDetailsService,
    final PasswordEncoder passwordEncoder) {
    final DaoAuthenticationProvider provider = 
        new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
}
```

**Note:** In Spring Security 6+, `DaoAuthenticationProvider` now requires `UserDetailsService` as a constructor parameter (not a setter).

### 3. Updated SecurityConfig
**File:** `src/main/java/com/bacpham/saas/security/SecurityConfig.java`

Removed the `@EnableMethodSecurity` annotation since:
- Your application doesn't use method-level security annotations
- Removing it eliminates unnecessary AOP proxy creation
- This reduces the chance of proxy-related issues

```java
@Configuration
@EnableWebSecurity  // Removed @EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    // ... rest of the config
}
```

## How It Works

1. When a user calls `/api/v1/auth/login`:
   - The `AuthenticationController` calls `AuthenticationServiceImpl.login()`
   - This calls `authenticationManager.authenticate(UsernamePasswordAuthenticationToken)`

2. The `AuthenticationManager` now has a properly configured `DaoAuthenticationProvider`:
   - It calls `CustomUserDetailsService.loadUserByUsername()` to fetch the user
   - It compares the provided password with the stored hash using `PasswordEncoder`
   - If valid, it returns an authenticated token

3. The authentication succeeds without proxy recursion issues

## Testing

To verify the fix works:

1. Make sure you have a User record in your database with:
   - A valid username
   - A password (should be BCrypt hashed)
   - The `enabled` field set to `true`

2. Send a POST request to `/api/v1/auth/login`:
   ```json
   {
       "username": "your_username",
       "password": "your_password"
   }
   ```

3. You should receive a JWT token in response without any StackOverflowError.

## Additional Notes

- Your `User` entity correctly implements `UserDetails`, which Spring Security expects
- The `User` entity has an `enabled` field - make sure users are created with `enabled = true`
- The JWT filter (`JwtAuthenticationFilter`) continues to handle token validation for subsequent requests
- Your tenant context and schema resolution are preserved in the authentication flow

