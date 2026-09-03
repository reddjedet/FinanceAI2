package com.financeai.transactions.repository;

import com.financeai.transactions.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    List<Transaccion> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    List<Transaccion> findByUsuarioIdAndFechaBetweenOrderByFechaDesc(Long usuarioId, LocalDate desde, LocalDate hasta);

    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t WHERE t.usuarioId = :usuarioId AND MONTH(t.fecha) = :mes AND YEAR(t.fecha) = :anio")
    BigDecimal obtenerTotalGastadoEnMes(@Param("usuarioId") Long usuarioId, @Param("mes") int mes, @Param("anio") int anio);

    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t WHERE t.usuarioId = :usuarioId AND t.categoria IN ('Creditos y Deudas', 'Vivienda', 'Servicios') AND t.fecha BETWEEN :desde AND :hasta")
    BigDecimal sumarGastosFijosPorRango(@Param("usuarioId") Long usuarioId, @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT COUNT(t) FROM Transaccion t WHERE t.usuarioId = :usuarioId AND t.categoria = 'Inversion' AND t.fecha BETWEEN :desde AND :hasta")
    long contarTransaccionesInversionPorRango(@Param("usuarioId") Long usuarioId, @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}
