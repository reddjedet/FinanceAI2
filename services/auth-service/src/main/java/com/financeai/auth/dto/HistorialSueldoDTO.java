package com.financeai.auth.dto;

import com.financeai.auth.model.HistorialSueldo;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HistorialSueldoDTO(
    Long id,
    Long usuarioId,
    BigDecimal sueldoAnterior,
    BigDecimal sueldoNuevo,
    LocalDateTime fechaModificacion
) {
    public HistorialSueldoDTO(HistorialSueldo entidad) {
        this(
            entidad.getId(),
            entidad.getUsuarioId(),
            entidad.getSueldoAnterior(),
            entidad.getSueldoNuevo(),
            entidad.getFechaModificacion()
        );
    }
}
