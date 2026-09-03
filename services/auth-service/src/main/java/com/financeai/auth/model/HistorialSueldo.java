package com.financeai.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_sueldo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialSueldo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "sueldo_anterior", nullable = false, precision = 12, scale = 2)
    private BigDecimal sueldoAnterior;

    @Column(name = "sueldo_nuevo", nullable = false, precision = 12, scale = 2)
    private BigDecimal sueldoNuevo;

    @Column(name = "fecha_modificacion", nullable = false, updatable = false)
    private LocalDateTime fechaModificacion;

    @PrePersist
    public void prePersist() {
        if (this.fechaModificacion == null) {
            this.fechaModificacion = LocalDateTime.now();
        }
    }
}
