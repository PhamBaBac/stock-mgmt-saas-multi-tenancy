package com.bacpham.saas.services.impl;

import com.bacpham.saas.common.PageResponse;
import com.bacpham.saas.entities.Product;
import com.bacpham.saas.entities.StockMvt;
import com.bacpham.saas.entities.TypeMvt;
import com.bacpham.saas.mappers.StockMvtMapper;
import com.bacpham.saas.repositories.PartnerRepository;
import com.bacpham.saas.repositories.ProductRepository;
import com.bacpham.saas.repositories.StockMvtRepository;
import com.bacpham.saas.requests.StockMvtRequest;
import com.bacpham.saas.responses.StockMvtResponse;
import com.bacpham.saas.services.StockMvtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockMvtServiceImpl implements StockMvtService {

    private final StockMvtRepository stockMvtRepository;
    private final ProductRepository productRepository;
    private final PartnerRepository partnerRepository;
    private final StockMvtMapper stockMvtMapper;

    @Override
    @Transactional
    public void create(final StockMvtRequest request) {
        log.info("Recording stock movement for product: {}", request.getProductId());
        
        final Product product = this.productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (request.getPartnerId() != null) {
            this.partnerRepository.findById(request.getPartnerId())
                    .orElseThrow(() -> new EntityNotFoundException("Partner not found"));
        }

        final StockMvt entity = this.stockMvtMapper.toEntity(request);
        if (entity.getDateMvt() == null) {
            entity.setDateMvt(LocalDate.now());
        }
        
        this.stockMvtRepository.save(entity);

        // Update product available quantity
        int currentQty = product.getAvailableQuantity();
        if (TypeMvt.IN.equals(request.getTypeMvt())) {
            product.setAvailableQuantity(currentQty + request.getQuantity());
        } else {
            product.setAvailableQuantity(currentQty - request.getQuantity());
        }
        
        this.productRepository.save(product);
        log.info("Stock movement recorded and product quantity updated");
    }

    @Override
    @Transactional
    public void update(final String id, final StockMvtRequest request) {
        final StockMvt stockMvt = this.stockMvtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("StockMvt does not exist"));

        if (request.getPartnerId() != null) {
            this.partnerRepository.findById(request.getPartnerId())
                    .orElseThrow(() -> new EntityNotFoundException("Partner not found"));
        }

        final StockMvt stockMvtToUpdate = this.stockMvtMapper.toEntity(request);
        stockMvtToUpdate.setId(id);
        if (stockMvtToUpdate.getDateMvt() == null) {
            stockMvtToUpdate.setDateMvt(LocalDate.now());
        }
        this.stockMvtRepository.save(stockMvtToUpdate);
    }

    @Override
    public PageResponse<StockMvtResponse> findAll(final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<StockMvt> stockMvts = this.stockMvtRepository.findAll(pageRequest);
        final Page<StockMvtResponse> stockMvtResponses = stockMvts.map(this.stockMvtMapper::toResponse);
        return PageResponse.of(stockMvtResponses);
    }

    @Override
    public StockMvtResponse findById(final String id) {
        return this.stockMvtRepository.findById(id)
                .map(this.stockMvtMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("StockMvt does not exist"));
    }

    @Override
    @Transactional
    public void delete(final String id) {
        final StockMvt stockMvt = this.stockMvtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("StockMvt does not exist"));
        this.stockMvtRepository.delete(stockMvt);
    }

    @Override
    public PageResponse<StockMvtResponse> findAllByProductId(final String productId, final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<StockMvt> stockMvts = this.stockMvtRepository.findAllByProductId(productId, pageRequest);
        final Page<StockMvtResponse> stockMvtResponses = stockMvts.map(this.stockMvtMapper::toResponse);
        return PageResponse.of(stockMvtResponses);
    }
}
