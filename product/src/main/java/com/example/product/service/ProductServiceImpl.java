package com.example.product.service;

import com.example.product.dto.request.ProductRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.mapper.ProductMapper;
import com.example.product.model.Category;
import com.example.product.model.Product;
import com.example.product.repository.CategoryRepository;
import com.example.product.repository.ProductRepository;
import com.example.product.utils.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public boolean isExists(String name) {
        return productRepository.existsByName(name);
    }

    public Page<Product> findAll(Pageable pageable) {
        Page<Product> products = productRepository.findAllByDeletedIsFalse(pageable);
        return products;
    }

    public ProductResponse findById(Long productId) {
        ProductResponse productResponse =
                productMapper.productToProductResponse(productRepository.findById(productId).orElse(null));
        return productResponse;
    }

    public List<ProductResponse> findAllByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findAllByCategory_Id(categoryId);

        List<ProductResponse> productResponses = new ArrayList<>();

        for (Product product : products) {
            ProductResponse productResponse = findById(product.getId());
            productResponses.add(productResponse);
        }

        return productResponses;
    }

    public List<ProductResponse> search(String keyword) {
        List<Product> products = productRepository.findAllByNameContaining(keyword);
        List<ProductResponse> productResponses = new ArrayList<>();

        for (Product product : products) {
            ProductResponse productResponse = findById(product.getId());
            productResponses.add(productResponse);
        }

        return productResponses;
    }

    public Product save(ProductRequest productRequest) {
        Product product = productMapper.productRequestDTOToProduct(productRequest);
        Category category = categoryRepository.findById(productRequest.getCategoryId()).orElse(null);
        if (category == null) {
            throw new RuntimeException("Không có danh mục");
        }
        product.setCategory(category);
        product.setImage(UploadUtils.save(productRequest.getImage()));
        return productRepository.save(product);
    }

    public ProductResponse update(Long productId, ProductRequest productRequest) {
        Product existingProduct = productRepository.findById(productId).orElse(null);
        if (existingProduct == null) {
            throw new RuntimeException("Sản phẩm này không có trong hệ thống");
        } else if (productRepository.existsByNameAndIdIsNot(productRequest.getName(), productId)) {
            throw new RuntimeException("Sản phẩm này đã tồn tại");
        } else {
            Product product = productMapper.productRequestDTOToProduct(productRequest);
            product.setId(existingProduct.getId());
            Category category = categoryRepository.findById(productRequest.getCategoryId()).orElse(null);
            if (category == null) {
                throw new RuntimeException("Không có danh mục");
            }
            product.setCategory(category);
            product.setImage(UploadUtils.save(productRequest.getImage()));
            ProductResponse productResponse = ProductMapper.INSTANCE.productToProductResponse(productRepository.save(product));
            return productResponse;
        }
    }

    public void delete(Long productId) {
        Product existingProduct = productRepository.findById(productId).orElse(null);

        if (existingProduct != null) {
            existingProduct.setDeleted(true);
            productRepository.save(existingProduct);
        } else {
            throw new RuntimeException("Sản phẩm không tồn tại");
        }
    }

}
