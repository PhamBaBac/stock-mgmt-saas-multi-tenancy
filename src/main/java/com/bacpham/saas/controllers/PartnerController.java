package com.bacpham.saas.controllers;

import com.bacpham.saas.common.PageResponse;
import com.bacpham.saas.requests.PartnerRequest;
import com.bacpham.saas.responses.PartnerResponse;
import com.bacpham.saas.services.PartnerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/partners")
@RequiredArgsConstructor
@Tag(name = "Partners")
public class PartnerController {

    private final PartnerService partnerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPartner(@RequestBody @Valid final PartnerRequest request) {
        this.partnerService.create(request);
    }

    @PutMapping("/{partner-id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updatePartner(
            @PathVariable("partner-id") final String partnerId,
            @RequestBody @Valid final PartnerRequest request) {
        this.partnerService.update(partnerId, request);
    }

    @GetMapping("/{partner-id}")
    public ResponseEntity<PartnerResponse> findPartnerById(@PathVariable("partner-id") final String partnerId) {
        return ResponseEntity.ok(this.partnerService.findById(partnerId));
    }

    @DeleteMapping("/{partner-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePartner(@PathVariable("partner-id") final String partnerId) {
        this.partnerService.delete(partnerId);
    }

    @GetMapping
    public ResponseEntity<PageResponse<PartnerResponse>> findAllPartners(
            @RequestParam(name = "page", defaultValue = "0", required = false) final int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) final int size
    ) {
        return ResponseEntity.ok(this.partnerService.findAll(page, size));
    }
}
