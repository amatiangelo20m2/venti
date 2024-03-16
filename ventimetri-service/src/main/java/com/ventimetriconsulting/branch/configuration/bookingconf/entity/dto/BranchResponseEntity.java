package com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto;

import com.ventimetriconsulting.branch.entity.Role;
import com.ventimetriconsulting.branch.entity.dto.BranchType;
import com.ventimetriconsulting.supplier.dto.SupplierDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@ToString
public class BranchResponseEntity {

    private String name;
    private String address;
    private String email;
    private String phone;
    private String vat;
    private BranchType type;
    private String branchCode;
    private Role role;
    private byte[] logoImage;
    private BranchConfigurationDTO branchConfigurationDTO;
    private List<SupplierDTO> supplierDTOList;
}
