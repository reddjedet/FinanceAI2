package com.financeai.transactions.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "analisis_financiero")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalisisFinanciero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "fecha_analisis", nullable = false)
    private LocalDateTime fechaAnalisis;

    @Column(name = "rango_inicio", nullable = false)
    private LocalDate rangoInicio;

    @Column(name = "rango_fin", nullable = false)
    private LocalDate rangoFin;

    @Column(name = "perfil_financiero", nullable = false, length = 100)
    private String perfilFinanciero;

    @Column(name = "porcentaje_endeudamiento", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeEndeudamiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "frecuencia_ahorro", nullable = false, length = 50)
    private FrecuenciaAhorro frecuenciaAhorro;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String recomendaciones;

    @PrePersist
    public void prePersist() {
        if (this.fechaAnalisis == null) {
            this.fechaAnalisis = LocalDateTime.now();
        }
    }
}
