package com.ventimetriconsulting.inventario.entity.exrta;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryAction {

    @Column(name = "update_time", nullable = false)
    private Date updateDate;
    @Column(name = "inserted_amount", nullable = false)
    private long insertedAmount;
    @Column(name = "removed_amount", nullable = false)
    private long removedAmount;
    @Column(name = "modified_by_user", nullable = false)
    private String modifiedByUser;

//    public static String toJsonString(Date updateDate,
//                                      int insertedAmount,
//                                      int removedAmount,
//                                      String modifiedByUser) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        InventoryAction inventoryAction = new InventoryAction(updateDate, insertedAmount, removedAmount, modifiedByUser);
//        try {
//            // Create a list with a single InventoryAction object
//            List<InventoryAction> inventoryActionList = new ArrayList<>();
//            inventoryActionList.add(inventoryAction);
//            // Serialize the list to JSON
//            return objectMapper.writeValueAsString(inventoryActionList);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            return ""; // Return empty string if JSON processing fails
//        }
//    }
//
//    // Static method to convert InventoryAction list to JSON string
//    public static String toJsonString(List<InventoryAction> inventoryActions) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            return objectMapper.writeValueAsString(inventoryActions);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace(); // Handle the exception or log it
//            return ""; // Return empty string if JSON processing fails
//        }
//    }
//
//    // Static method to convert JSON string to InventoryAction list
//    public static List<InventoryAction> fromJsonString(String jsonString) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            return objectMapper.readValue(jsonString, new TypeReference<List<InventoryAction>>() {});
//        } catch (JsonProcessingException e) {
//            e.printStackTrace(); // Handle the exception or log it
//            return new ArrayList<>(); // Return empty list if JSON processing fails
//        }
//    }
}
