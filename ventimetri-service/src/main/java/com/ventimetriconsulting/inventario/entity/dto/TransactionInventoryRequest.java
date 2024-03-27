package com.ventimetriconsulting.inventario.entity.dto;


import com.ventimetriconsulting.inventario.entity.exrta.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInventoryRequest {

    private String user;
    private long storageId;
    List<TransactionItem> transactionItemList;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransactionItem {
        private long productId;
        private TransactionType transactionType;
        private long amount;
    }
}
