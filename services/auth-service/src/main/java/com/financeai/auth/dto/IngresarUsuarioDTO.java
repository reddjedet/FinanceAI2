package com.financeai.auth.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record IngresarUsuarioDTO(
    @NotBlank(message = "El nombre es obligatorio.")
    String nombre,

    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "El formato de email no es válido.")
    String email,

    @NotBlank(message = "La contraseña es obligatoria.")
    String password,

    @NotNull(message = "El ingreso mensual es obligatorio.")
    @DecimalMin(value = "0.01", message = "El ingreso mensual debe ser mayor a cero.")
    BigDecimal ingresoMensual
) {}
