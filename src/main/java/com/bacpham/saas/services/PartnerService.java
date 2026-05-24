package com.bacpham.saas.services;

import com.bacpham.saas.common.PageResponse;
import com.bacpham.saas.requests.PartnerRequest;
import com.bacpham.saas.responses.PartnerResponse;

public interface PartnerService extends BasicService<PartnerRequest, PartnerResponse> {
    PageResponse<PartnerResponse> findAll(int page, int size);
}
