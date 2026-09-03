package com.financeai.transactions.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserProfileDTO(
    Long id,
    String nombre,
    String email,
    BigDecimal ingresoMensual,
    Boolean activo
) {}
