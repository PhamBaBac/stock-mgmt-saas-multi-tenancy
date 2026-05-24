package com.bacpham.saas.requests;

import com.bacpham.saas.entities.PartnerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class PartnerRequest {

    @NotBlank(message = "Partner name should not be empty")
    @Size(min = 2, max = 255, message = "Partner name should be between 2 and 255 characters")
    private String name;

    private String email;
    private String phoneNumber;
    private String address;
    private String taxId;

    @NotNull(message = "Partner type is required")
    private PartnerType type;
}
