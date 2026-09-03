package com.financeai.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DatosLoginDTO(
    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "El formato de email es inválido.")
    String email,

    @NotBlank(message = "La contraseña es obligatoria.")
    String password
) {}
