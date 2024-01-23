package com.venticonsulting.branchconf.bookingconf.entity.dto;

import com.venticonsulting.branchconf.bookingconf.entity.configuration.BranchTimeRange;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class CustomerFormData implements Serializable {

    private String branchCode;
    private String formCode;
    private String logoImage;
    private int guests;
    private int bookingSlotInMinutes;
    private int maxTableNumber;
    private List<BranchTimeRange> timeRange;
    private Map<String, Integer> btime;
}
