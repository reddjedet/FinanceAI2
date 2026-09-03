package com.financeai.auth.repository;

import com.financeai.auth.model.HistorialSueldo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialSueldoRepository extends JpaRepository<HistorialSueldo, Long> {
    List<HistorialSueldo> findByUsuarioIdOrderByFechaModificacionDesc(Long usuarioId);
}
