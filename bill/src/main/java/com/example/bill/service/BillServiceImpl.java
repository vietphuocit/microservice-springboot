package com.example.bill.service;

import com.example.bill.dto.request.BillRequest;
import com.example.bill.feign.APIProduct;
import com.example.bill.mapper.BillMapper;
import com.example.bill.model.Bill;
import com.example.bill.repository.BillRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BillServiceImpl {

    @Autowired
    private final APIProduct apiProduct;

    @Autowired
    private BillMapper billMapper;

    @Autowired
    private BillRepository billRepository;

    public Page<Bill> findAll(Pageable pageable){
        Page<Bill> bills = billRepository.findAll(pageable);
        return bills;
    }

    public BillRequest save(BillRequest billRequest) {
        if(apiProduct.getProductById(billRequest.getProductId()) == null) {
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }
        int stock = apiProduct.getQuantityProduct(billRequest.getProductId());
        if(billRequest.getQuantity() > stock) {
            throw new RuntimeException("Còn " + apiProduct.getQuantityProduct(billRequest.getProductId()) + " sản " +
                    "phẩm thui");
        }
        billRepository.save(billMapper.billRequestDTOToBill(billRequest));

        return billRequest;
    }

    public void delete(Long billId) {
        Optional<Bill> existsBill = billRepository.findById(billId);
        if (existsBill.isPresent()){
            billRepository.deleteById(billId);
        } else {
            throw new RuntimeException("Hóa đơn không tồn tại");
        }
    }
}


