package com.financeai.transactions.repository;

import com.financeai.transactions.model.AnalisisFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalisisFinancieroRepository extends JpaRepository<AnalisisFinanciero, Long> {
    List<AnalisisFinanciero> findByUsuarioIdOrderByFechaAnalisisDesc(Long usuarioId);
}
