package com.bacpham.saas.services;

import com.bacpham.saas.requests.ProductRequest;
import com.bacpham.saas.responses.ProductResponse;

public interface ProductService extends BasicService<ProductRequest, ProductResponse> {
    void updateStatus(String id, boolean active);
}