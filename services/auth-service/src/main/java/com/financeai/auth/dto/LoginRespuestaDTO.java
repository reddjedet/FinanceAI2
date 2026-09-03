package com.financeai.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record LoginRespuestaDTO(
    String token,
    String tipo,
    Long id,
    @JsonProperty("idUsuario") Long idUsuario,
    String nombre,
    String email,
    BigDecimal ingresoMensual
) {
    public LoginRespuestaDTO(String token, Long id, String nombre, String email, BigDecimal ingresoMensual) {
        this(token, "Bearer", id, id, nombre, email, ingresoMensual);
    }
}
