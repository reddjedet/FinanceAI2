package com.financeai.transactions.dto;

import com.financeai.transactions.model.ResumenMensual;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RespuestaResumenMensualDTO(
    Long id,
    Long usuarioId,
    Integer mes,
    Integer anio,
    BigDecimal ingresoFijo,
    BigDecimal totalGastado,
    BigDecimal sobranteFinal,
    LocalDateTime fechaCierre
) {
    public RespuestaResumenMensualDTO(ResumenMensual r) {
        this(r.getId(), r.getUsuarioId(), r.getMes(), r.getAnio(), r.getIngresoFijo(), r.getTotalGastado(), r.getSobranteFinal(), r.getFechaCierre());
    }
}
