package tech.wvs.jbankapp.exception.dto;

public record InvalidParamDto(String field,
                              String reason) {
}
