package com.financeai.transactions.dto;

import com.financeai.transactions.model.Transaccion;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RespuestaTransaccionDTO(
    Long id,
    Long usuarioId,
    String descripcion,
    BigDecimal monto,
    String categoria,
    LocalDate fecha,
    LocalDateTime fechaCreacion
) {
    public RespuestaTransaccionDTO(Transaccion t) {
        this(t.getId(), t.getUsuarioId(), t.getDescripcion(), t.getMonto(), t.getCategoria(), t.getFecha(), t.getFechaCreacion());
    }
}
