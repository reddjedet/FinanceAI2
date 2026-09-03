package com.financeai.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ActualizarTransaccionDTO(
    String descripcion,
    BigDecimal monto,
    String categoria,
    LocalDate fecha
) {}
