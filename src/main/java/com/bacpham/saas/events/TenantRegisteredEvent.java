package com.bacpham.saas.events;

import org.springframework.context.ApplicationEvent;

public class TenantRegisteredEvent extends ApplicationEvent {
    private final String companyName;
    private final String companyCode;
    private final String adminEmail;

    public TenantRegisteredEvent(Object source, String companyName, String companyCode, String adminEmail) {
        super(source);
        this.companyName = companyName;
        this.companyCode = companyCode;
        this.adminEmail = adminEmail;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public String getAdminEmail() {
        return adminEmail;
    }
}
