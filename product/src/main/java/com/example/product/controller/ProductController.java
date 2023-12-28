package com.example.product.controller;

import com.example.product.dto.request.ProductRequest;
import com.example.product.model.Product;
import com.example.product.service.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductServiceImpl productService;

    @GetMapping(path = {"/", ""})
    public ResponseEntity<?> product(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortType) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.Direction.fromString(sortType), sortBy);
        Page<Product> products = productService.findAll(pageable);

        return ResponseEntity.ok(products);
    }

    @GetMapping(path ="/search")
    public ResponseEntity<?> searchProduct(@Param("keyword") String keyword) {
        return ResponseEntity.ok(productService.search(keyword));
    }

    @GetMapping(path = {"/{productId", "/{productId}/"})
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.findById(productId));
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<?> addProduct(@ModelAttribute ProductRequest productRequest) {
        if (productService.isExists(productRequest.getName())) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                    "message", "Sản phẩm đã tồn tại trong hệ thông"));
        }
        try {
            Product product = productService.save(productRequest);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                    "message", e.getMessage()));
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> editProduct(@PathVariable Long productId,
                                         @ModelAttribute ProductRequest productRequest) {
        try {
            return ResponseEntity.ok(productService.update(productId, productRequest));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                    "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        try {
            productService.delete(productId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                    "message", e.getMessage()));
        }
    }
}
