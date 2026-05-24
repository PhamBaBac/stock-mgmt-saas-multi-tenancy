package com.bacpham.saas.services.impl;

import com.bacpham.saas.common.PageResponse;
import com.bacpham.saas.entities.Category;
import com.bacpham.saas.entities.Product;
import com.bacpham.saas.exceptions.DuplicateResourceException;
import com.bacpham.saas.mappers.ProductMapper;
import com.bacpham.saas.repositories.CategoryRepository;
import com.bacpham.saas.repositories.ProductRepository;
import com.bacpham.saas.repositories.StockMvtRepository;
import com.bacpham.saas.requests.ProductRequest;
import com.bacpham.saas.responses.ProductResponse;
import com.bacpham.saas.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockMvtRepository stockMvtRepository;
    private final ProductMapper productMapper;
    @Override
    public void create(ProductRequest request) {
// check if product already exists
        checkIfProductAlreadyExistsByReference(request.getReference());

        // check if category exists
        checkIfCategoryExistById(request.getCategoryId());

        final Product entity = this.productMapper.toEntity(request);
        this.productRepository.save(entity);
    }

    @Override
    public void update(String id, ProductRequest request) {
       // check if product exists
        final Optional<Product> productExists = this.productRepository.findById(id);
        if (productExists.isEmpty()) {
            log.debug("Product does not exist");
            throw new EntityNotFoundException("Product does not exist");
        }

        // check if product already exists
        if (!productExists.get().getReference().equalsIgnoreCase(request.getReference())) {
            checkIfProductAlreadyExistsByReference(request.getReference());
        }

        // check if category exists
        checkIfCategoryExistById(request.getCategoryId());

        final Product productToUpdate = this.productMapper.toEntity(request);
        productToUpdate.setId(id);
        this.productRepository.save(productToUpdate);
    }

    @Override
    public PageResponse<ProductResponse> findAll(int page, int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<Product> products = this.productRepository.findAll(pageRequest);
        final Page<ProductResponse> productResponses = products.map(this.productMapper::toResponse);
        return PageResponse.of(productResponses);
    }

    @Override
    public ProductResponse findById(String id) {
        return this.productRepository.findById(id)
                .map(this.productMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product does not exist"));
    }

    @Override
    public void delete(String id) {
        final Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product does not exist"));

        if (this.stockMvtRepository.existsByProductId(id)) {
            throw new IllegalStateException("Không thể xóa sản phẩm này vì đã phát sinh lịch sử giao dịch kho. Vui lòng chuyển sang trạng thái Ngừng hoạt động (Inactive) thay vì xóa.");
        }

        this.productRepository.delete(product);
    }

    private void checkIfProductAlreadyExistsByReference(final String reference) {
        final Optional<Product> product = this.productRepository.findByReferenceIgnoreCase(reference);
        if (product.isPresent()) {
            log.debug("Product already exists");
            throw new DuplicateResourceException("Product already exists");
        }
    }

    private void checkIfCategoryExistById(final String categoryId) {
        final Optional<Category> category = this.categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            log.debug("Category does not exist");
            throw new EntityNotFoundException("Category does not exist");
        }
    }
}
