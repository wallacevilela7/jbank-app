package tech.wvs.jbankapp.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferMoneyDto(
        @NotNull UUID senderId,
        @NotNull  UUID receiverId,
        @NotNull @DecimalMin("0.01") BigDecimal value) {
}
