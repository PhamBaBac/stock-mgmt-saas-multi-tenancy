package com.bacpham.saas.mappers;

import com.bacpham.saas.entities.Category;
import com.bacpham.saas.entities.Product;
import com.bacpham.saas.requests.ProductRequest;
import com.bacpham.saas.responses.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(final ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .reference(request.getReference())
                .description(request.getDescription())
                .price(request.getPrice())
                .alertThreshold(request.getAlertThreshold())
                .category(Category.builder()
                        .id(request.getCategoryId())
                        .build())
                .active(request.getActive() != null ? request.getActive() : true)
                .deleted(false)
                .build();
    }

    public ProductResponse toResponse(final Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .reference(product.getReference())
                .description(product.getDescription())
                .price(product.getPrice())
                .alertThreshold(product.getAlertThreshold())
                .categoryId(product.getCategory()
                        .getId())
                .categoryName(product.getCategory()
                        .getName())
                .availableQuantity(product.getAvailableQuantity())
                .active(product.isActive())
                .build();
    }
}
