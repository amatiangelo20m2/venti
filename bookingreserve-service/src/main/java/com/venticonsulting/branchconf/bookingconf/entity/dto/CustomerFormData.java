package com.venticonsulting.branchconf.bookingconf.entity.dto;

import com.venticonsulting.branchconf.bookingconf.entity.configuration.BranchTimeRange;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class CustomerFormData implements Serializable {
    private String branchCode;
    private String formCode;
    private byte[] logoImage;
    private List<BranchTimeRange> branchTimeRanges;
    private int bookingSlotInMinutes;



}
