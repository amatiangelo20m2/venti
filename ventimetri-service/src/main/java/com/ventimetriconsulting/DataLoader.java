package com.ventimetriconsulting;

import com.netflix.discovery.converters.Auto;
import com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto.BranchResponseEntity;
import com.ventimetriconsulting.branch.controller.BranchController;
import com.ventimetriconsulting.branch.entity.dto.BranchCreationEntity;
import com.ventimetriconsulting.branch.entity.dto.BranchType;
import com.ventimetriconsulting.supplier.controller.SupplierController;
import com.ventimetriconsulting.supplier.dto.ProductDTO;
import com.ventimetriconsulting.supplier.dto.SupplierDTO;
import com.ventimetriconsulting.supplier.entity.UnitMeasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class DataLoader implements CommandLineRunner {

    private BranchController branchController;
    private SupplierController supplierController;

    @Autowired
    public DataLoader(BranchController branchController, SupplierController supplierController) {
        this.branchController = branchController;
        this.supplierController = supplierController;
    }

    @Override
    public void run(String... args) throws Exception {
        String userCode = "0000000000";

        BranchCreationEntity fakeBranch = BranchCreationEntity.builder()
                .branchCode("")
                .userCode(userCode)
                .name("20m2")
                .email("samplebranch@example.com")
                .address("123 Fake Street")
                .city("Faketown")
                .cap("12345")
                .phoneNumber("555-1234")
                .vat("VAT123456")
                .type(BranchType.RESTAURANT)
                .logoImage(new byte[]{})
                .build();

        ResponseEntity<BranchResponseEntity> savedBranch = branchController.save(fakeBranch);
        ResponseEntity<SupplierDTO> supplierDTOResponseEntity = supplierController.addSupplier(createTestSupplierDTO("Fornitore Default"), savedBranch.getBody().getBranchCode());

        List<ProductDTO> products = new ArrayList<>();
        for (String dish : productNameList) {
            ProductDTO product = getProductInstance(dish);
            products.add(product);
        }


        ResponseEntity<List<ProductDTO>> listResponseEntity = supplierController.insertProductList(products,
                Objects.requireNonNull(supplierDTOResponseEntity.getBody()).getSupplierId());




    }


    String[] productNameList = {
            "Hamburger di manzo",
            "Hamburger ripieno",
            "Pollo croccante",
            "Tagliata di pollo",
            "Tagliata di manzo",
            "Tartare di manzo",
            "Tartare di tonno",
            "Polpo",
            "Stracciata di melanzane",
            "Polpette di carne",
            "Polpette di pane",
            "Pulled pork",
            "Cipolla caramellata",
            "Patate al forno",
            "Guacamole",
            "Salsa poke 20m2",
            "Salsa burger 20m2",
            "Maio sweet chili",
            "Maio teriaki",
            "Sauce cheddar",
            "Maio Japan",
            "Maio plic plac",
            "Maio rosmarino",
            "Bombette",
            "Focaccia",
            "Crema mascarpone",
            "Tiramisù",
            "Salame al cioccolato",
            "Cheesecake"
    };

    public static SupplierDTO createTestSupplierDTO(String name) {
        SupplierDTO dto = new SupplierDTO();
        dto.setName(name);
        dto.setVatNumber("123456789");
        dto.setAddress("123 Test Address");
        dto.setCity("Test City");
        dto.setCap("12345");
        dto.setPhoneNumber("123-456-7890");
        dto.setEmail("test@supplier.com");
        dto.setPec("test@pec.supplier.com");
        dto.setCf("CF123456789");
        dto.setCountry("Test Country");
        dto.setCreatedByUserId(1L);
        return dto;
    }

    public static ProductDTO getProductInstance(String productName) {
        return ProductDTO.builder()
                .productId(0)
                .name(productName)
                .unitMeasure(UnitMeasure.KG)
                .vatApplied((int) (Math.random() * 100))
                .price(Math.random() * 1000)
                .productCode("")
                .description("Random description")
                .category("Random category")
                .sku("Random SKU")
                .build();
    }

}
