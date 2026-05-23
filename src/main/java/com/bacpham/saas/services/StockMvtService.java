package com.bacpham.saas.services;

import com.bacpham.saas.common.PageResponse;
import com.bacpham.saas.requests.StockMvtRequest;
import com.bacpham.saas.responses.StockMvtResponse;

public interface StockMvtService extends BasicService<StockMvtRequest, StockMvtResponse> {

    PageResponse<StockMvtResponse> findAllByProductId(final String productId, final int page, final int size);
}