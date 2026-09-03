package com.financeai.transactions.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "resumenes_mensuales", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "mes", "anio"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenMensual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer anio;

    @Column(name = "ingreso_fijo", nullable = false, precision = 12, scale = 2)
    private BigDecimal ingresoFijo;

    @Column(name = "total_gastado", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalGastado;

    @Column(name = "sobrante_final", nullable = false, precision = 12, scale = 2)
    private BigDecimal sobranteFinal;

    @Column(name = "fecha_cierre", nullable = false)
    private LocalDateTime fechaCierre;

    @PrePersist
    public void prePersist() {
        if (this.fechaCierre == null) {
            this.fechaCierre = LocalDateTime.now();
        }
    }
}
