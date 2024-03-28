package com.ventimetriconsulting.inventario.entity.dto;


import com.ventimetriconsulting.inventario.entity.extra.TransactionType;
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
    private TransactionType transactionType;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransactionItem {
        private long productId;
        private long amount;
    }
}
