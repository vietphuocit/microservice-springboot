package com.example.product.controller;

import com.example.product.dto.request.CategoryRequest;
import com.example.product.dto.response.CategoryResponse;
import com.example.product.model.Category;
import com.example.product.service.CategoryServiceImpl;
import com.example.product.service.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private CategoryServiceImpl categoryService;

    @GetMapping(path = {"/", ""})
    public ResponseEntity<?> getCategory(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "5") int size,
                                      @RequestParam(defaultValue = "id") String sortBy,
                                      @RequestParam(defaultValue = "ASC") String sortType) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.Direction.fromString(sortType), sortBy);
        Page<Category> categories = categoryService.findAll(pageable);

        return ResponseEntity.ok(categories);
    }

    @GetMapping(path = {"/{categoryId}/products", "/{categoryId}/products/"})
    public ResponseEntity<?> getProductByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.findAllByCategoryId(categoryId));
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<?> addCategory(@ModelAttribute CategoryRequest categoryRequest) {
        if (categoryService.isExists(categoryRequest.getName())) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                    "message", "Danh mục đã tồn tại trong hệ thông"));
        }
        Category category = categoryService.save(categoryRequest);

        return ResponseEntity.ok(category);
    }

    @PutMapping(path = {"/{categoryId}/", "/{categoryId}"})
    public ResponseEntity<?> editCategory(@PathVariable Long categoryId,
                                        @ModelAttribute CategoryRequest categoryRequest) {
        try {
            CategoryResponse categoryResponse = categoryService.findById(categoryId);

            if (categoryResponse.getName().equals(categoryRequest.getName())) {
                return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                        "message", "Dữ liệu không có gì thay đổi"));
            } else if (categoryService.isExists(categoryRequest.getName())) {
                return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                        "message", "Danh mục đã tồn tại trong hệ thông"));
            }
            return ResponseEntity.ok(categoryService.update(categoryId, categoryRequest));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 404,
                    "message", "Danh mục không tồn tại"));
        }
    }

    @DeleteMapping(path = {"/{categoryId}/", "/{categoryId}"})
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        if (categoryService.findById(categoryId) == null) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 404,
                    "message", "Danh mục không tồn tại"));
        }
        try {
            categoryService.delete(categoryId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 500,
                    "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 500,
                    "message", "Có lỗi khi xóa danh mục"));
        }
    }
}
