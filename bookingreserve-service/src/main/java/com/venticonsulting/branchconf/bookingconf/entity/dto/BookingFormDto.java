package com.venticonsulting.branchconf.bookingconf.entity.dto;

import com.venticonsulting.branchconf.bookingconf.entity.configuration.BookingForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingFormDto {
    private String formCode;
    private String formName;
    private boolean isDefaultForm;
    private List<BranchTimeRangeDTO> branchTimeRanges;

    public static BookingFormDto fromEntity(BookingForm bookingForm){
        return BookingFormDto.builder()
                .formCode(bookingForm.getFormCode())
                .formName(bookingForm.getFormName())
                .isDefaultForm(bookingForm.isDefaultForm())
                .branchTimeRanges(BranchTimeRangeDTO.convertList(bookingForm.getBranchTimeRanges()))
                .build();
    }

    public static List<BookingFormDto> convertList(List<BookingForm> bookingForms) {
        return bookingForms.stream()
                .map(BookingFormDto::fromEntity)
                .collect(Collectors.toList());
    }

}
