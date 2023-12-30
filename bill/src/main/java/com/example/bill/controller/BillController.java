package com.example.bill.controller;

import com.example.bill.dto.request.BillRequest;
import com.example.bill.model.Bill;
import com.example.bill.service.BillServiceImpl;
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
@RequestMapping("/api/bill")
public class BillController {

    @Autowired
    private BillServiceImpl billService;

    @GetMapping(path = {"/", ""})
    public ResponseEntity<?> bills(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "5") int size,
                                   @RequestParam(defaultValue = "id") String sortBy,
                                   @RequestParam(defaultValue = "ASC") String sortType) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.fromString(sortType), sortBy));
        Page<Bill> bills = billService.findAll(pageable);

        return ResponseEntity.ok(bills);
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<?> addBill(BillRequest billRequest) {
        try {
            billService.save(billRequest);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                    "message", e.getMessage()));
        }
    }

    @DeleteMapping(path = {"/{billId}", "/{billId}/"})
    public ResponseEntity<?> deleteBill(@PathVariable Long billId) {
        try {
            billService.delete(billId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errorCode", 400,
                    "message", e.getMessage()));
        }
    }

}
