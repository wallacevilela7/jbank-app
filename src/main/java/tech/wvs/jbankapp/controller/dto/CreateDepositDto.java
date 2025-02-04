package tech.wvs.jbankapp.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateDepositDto(
        @NotNull @DecimalMin("10.00") BigDecimal value) {
}
