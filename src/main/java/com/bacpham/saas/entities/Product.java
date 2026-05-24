package com.bacpham.saas.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "products")
@SQLDelete(sql = "UPDATE products SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
public class Product extends AbstractEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "alert_threshold", nullable = false)
    private Integer alertThreshold;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "available_quantity")
    private int availableQuantity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "active", nullable = false)
    @lombok.Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "product")
    private List<StockMvt> stockMovements;

}