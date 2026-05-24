package com.bacpham.saas.mappers;

import com.bacpham.saas.entities.Partner;
import com.bacpham.saas.entities.Product;
import com.bacpham.saas.entities.StockMvt;
import com.bacpham.saas.requests.StockMvtRequest;
import com.bacpham.saas.responses.StockMvtResponse;
import org.springframework.stereotype.Component;

@Component
public class StockMvtMapper {

    public StockMvt toEntity(final StockMvtRequest request) {
        return StockMvt.builder()
                .dateMvt(request.getDateMvt())
                .comment(request.getComment())
                .typeMvt(request.getTypeMvt())
                .quantity(request.getQuantity())
                .product(Product.builder()
                        .id(request.getProductId())
                        .build())
                .partner(request.getPartnerId() != null ? Partner.builder().id(request.getPartnerId()).build() : null)
                .deleted(false)
                .build();
    }

    public StockMvtResponse toResponse(final StockMvt entity) {
        return StockMvtResponse.builder()
                .id(entity.getId())
                .dateMvt(entity.getDateMvt())
                .comment(entity.getComment())
                .typeMvt(entity.getTypeMvt())
                .quantity(entity.getQuantity())
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .productName(entity.getProduct() != null ? entity.getProduct().getName() : null)
                .partnerId(entity.getPartner() != null ? entity.getPartner().getId() : null)
                .partnerName(entity.getPartner() != null ? entity.getPartner().getName() : null)
                .build();
    }
}