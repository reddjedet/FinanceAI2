package com.financeai.transactions.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record IngresarTransaccionDTO(
    @NotNull(message = "El ID de usuario es obligatorio.")
    Long idUsuario,

    @NotBlank(message = "La descripción de la transacción es obligatoria.")
    String descripcion,

    @NotNull(message = "El monto es obligatorio.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero.")
    BigDecimal monto,

    String categoria,

    @NotNull(message = "La fecha es obligatoria.")
    LocalDate fecha
) {}
