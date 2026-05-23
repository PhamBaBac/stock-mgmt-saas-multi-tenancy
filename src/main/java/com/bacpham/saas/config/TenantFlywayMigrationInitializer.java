package com.bacpham.saas.config;

import com.bacpham.saas.entities.Tenant;
import com.bacpham.saas.repositories.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantFlywayMigrationInitializer implements CommandLineRunner {

    private final TenantRepository tenantRepository;
    private final DataSource dataSource;

    @Override
    public void run(String... args) {
        log.info("Starting Flyway migrations for all existing tenants...");
        try {
            final List<Tenant> tenants = this.tenantRepository.findAll();
            for (final Tenant tenant : tenants) {
                final String schemaName = "tenant_" + tenant.getCompanyCode().toLowerCase();
                try {
                    runTenantMigrations(schemaName);
                } catch (final Exception e) {
                    log.error("Failed to run migrations for tenant schema: {}", schemaName, e);
                }
            }
            log.info("Flyway migrations for all existing tenants completed.");
        } catch (final Exception e) {
            log.error("Failed to fetch tenants for migrations", e);
        }
    }

    private void runTenantMigrations(final String schemaName) {
        log.info("Migrating schema: {}", schemaName);
        final Flyway tenantFlyway = Flyway.configure()
                .dataSource(this.dataSource)
                .schemas(schemaName)
                .locations("classpath:db/migration/tenant")
                .baselineOnMigrate(true)
                .table("flyway_schema_history")
                .validateOnMigrate(true)
                .cleanDisabled(true)
                .load();
        tenantFlyway.migrate();
    }
}
