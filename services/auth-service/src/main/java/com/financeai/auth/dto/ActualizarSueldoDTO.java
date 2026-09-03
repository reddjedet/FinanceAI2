package com.financeai.auth.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ActualizarSueldoDTO(
    @NotNull(message = "El nuevo sueldo es obligatorio.")
    @DecimalMin(value = "0.01", message = "El nuevo sueldo debe ser mayor a cero.")
    BigDecimal nuevoSueldo
) {}
