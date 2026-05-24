package com.bacpham.saas.mappers;

import com.bacpham.saas.entities.Partner;
import com.bacpham.saas.requests.PartnerRequest;
import com.bacpham.saas.responses.PartnerResponse;
import org.springframework.stereotype.Service;

@Service
public class PartnerMapper {

    public Partner toPartner(final PartnerRequest request) {
        if (request == null) {
            return null;
        }

        return Partner.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .taxId(request.getTaxId())
                .type(request.getType())
                .build();
    }

    public PartnerResponse toResponse(final Partner partner) {
        if (partner == null) {
            return null;
        }

        return PartnerResponse.builder()
                .id(partner.getId())
                .name(partner.getName())
                .email(partner.getEmail())
                .phoneNumber(partner.getPhoneNumber())
                .address(partner.getAddress())
                .taxId(partner.getTaxId())
                .type(partner.getType())
                .build();
    }
}
