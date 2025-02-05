package tech.wvs.jbankapp.controller.dto;

import tech.wvs.jbankapp.controller.enums.StatementOperation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StatementItemDto(String statementId,
                               String type,
                               String literal,
                               BigDecimal value,
                               LocalDateTime dateTime,
                               StatementOperation operation) {
}
