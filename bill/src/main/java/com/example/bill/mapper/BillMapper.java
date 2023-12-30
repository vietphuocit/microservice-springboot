package com.example.bill.mapper;

import com.example.bill.dto.request.BillRequest;
import com.example.bill.dto.response.BillResponse;
import com.example.bill.model.Bill;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BillMapper {
    BillMapper INSTANCE = Mappers.getMapper(BillMapper.class);

    BillResponse billToBillResponse(Bill bill);

    Bill billRequestDTOToBill(BillRequest billRequest);

}