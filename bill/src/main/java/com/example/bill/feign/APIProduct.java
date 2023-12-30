package com.example.bill.feign;


import com.example.bill.model.Product;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "product", url = "http://localhost:8080/api/product")
public interface APIProduct {

    @GetMapping("/{productId}")
    Product getProductById(@PathVariable Long productId);

    @GetMapping("/quantity/{productId}")
    int getQuantityProduct(@PathVariable Long productId);
}