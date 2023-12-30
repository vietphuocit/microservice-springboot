package com.example.product.repository;

import com.example.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    int countProductsByCategory_Id(Long id);

    List<Product> findAllByNameContaining(String name);

    List<Product> findAllByCategory_Id(Long id);

    boolean existsByName(String name);

    boolean existsByNameAndIdIsNot(String name, Long id);

    Page<Product> findAllByDeletedIsFalse(Pageable pageable);
}