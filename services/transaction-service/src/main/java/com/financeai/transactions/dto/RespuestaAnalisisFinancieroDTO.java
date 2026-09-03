package com.financeai.transactions.dto;

import com.financeai.transactions.model.AnalisisFinanciero;
import com.financeai.transactions.model.FrecuenciaAhorro;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RespuestaAnalisisFinancieroDTO(
    Long id,
    Long idUsuario,
    LocalDateTime fechaAnalisis,
    LocalDate rangoInicio,
    LocalDate rangoFin,
    String perfilFinanciero,
    BigDecimal porcentajeEndeudamiento,
    FrecuenciaAhorro nivelAhorro,
    String recomendaciones
) {
    public RespuestaAnalisisFinancieroDTO(AnalisisFinanciero a) {
        this(
            a.getId(),
            a.getUsuarioId(),
            a.getFechaAnalisis(),
            a.getRangoInicio(),
            a.getRangoFin(),
            a.getPerfilFinanciero(),
            a.getPorcentajeEndeudamiento(),
            a.getFrecuenciaAhorro(),
            a.getRecomendaciones()
        );
    }
}
