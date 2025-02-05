package tech.wvs.jbankapp.controller.dto;

import java.util.List;

public record StatementDto(WalletDto wallet,
                           List<StatementItemDto> statements,
                           PaginationDto pagination) {
}
