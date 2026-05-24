package com.bacpham.saas.services.impl;

import com.bacpham.saas.common.PageResponse;
import com.bacpham.saas.entities.Partner;
import com.bacpham.saas.mappers.PartnerMapper;
import com.bacpham.saas.repositories.PartnerRepository;
import com.bacpham.saas.requests.PartnerRequest;
import com.bacpham.saas.responses.PartnerResponse;
import com.bacpham.saas.services.PartnerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;
    private final PartnerMapper partnerMapper;

    @Override
    public void create(final PartnerRequest request) {
        log.info("Creating partner: {}", request.getName());
        final Partner partner = this.partnerMapper.toPartner(request);
        this.partnerRepository.save(partner);
    }

    @Override
    public void update(final String id, final PartnerRequest request) {
        final Partner existingPartner = this.partnerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partner does not exist"));

        final Partner partnerToUpdate = this.partnerMapper.toPartner(request);
        partnerToUpdate.setId(existingPartner.getId());
        this.partnerRepository.save(partnerToUpdate);
    }

    @Override
    public PartnerResponse findById(final String id) {
        return this.partnerRepository.findById(id)
                .map(this.partnerMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Partner does not exist"));
    }

    @Override
    public void delete(final String id) {
        final Partner partner = this.partnerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partner does not exist"));
        this.partnerRepository.delete(partner);
    }

    @Override
    public PageResponse<PartnerResponse> findAll(final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<Partner> partners = this.partnerRepository.findAll(pageRequest);
        final Page<PartnerResponse> partnerResponses = partners.map(this.partnerMapper::toResponse);
        return PageResponse.of(partnerResponses);
    }
}
