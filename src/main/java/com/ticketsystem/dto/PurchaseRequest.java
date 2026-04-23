package com.ticketsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PurchaseRequest {

    @NotNull
    private Long showId;

    @NotNull
    private Long zoneId;

    @NotEmpty
    private List<Long> seatIds;

    @NotBlank
    @Email
    private String buyerEmail;
}
