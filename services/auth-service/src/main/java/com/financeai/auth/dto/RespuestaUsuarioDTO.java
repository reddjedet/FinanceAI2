package com.financeai.auth.dto;

import com.financeai.auth.model.Usuario;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RespuestaUsuarioDTO(
    Long id,
    String nombre,
    String email,
    BigDecimal ingresoMensual,
    LocalDateTime fechaCreacion,
    Boolean activo
) {
    public RespuestaUsuarioDTO(Usuario usuario) {
        this(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getIngresoMensual(),
            usuario.getFechaCreacion(),
            usuario.getActivo()
        );
    }
}
