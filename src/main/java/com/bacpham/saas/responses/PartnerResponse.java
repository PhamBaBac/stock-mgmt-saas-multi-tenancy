package com.bacpham.saas.responses;

import com.bacpham.saas.entities.PartnerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartnerResponse {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String taxId;
    private PartnerType type;
}
