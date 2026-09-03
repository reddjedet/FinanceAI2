package com.financeai.transactions.repository;

import com.financeai.transactions.model.ResumenMensual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumenMensualRepository extends JpaRepository<ResumenMensual, Long> {
    Optional<ResumenMensual> findByUsuarioIdAndAnioAndMes(Long usuarioId, Integer anio, Integer mes);
    List<ResumenMensual> findByUsuarioIdOrderByAnioDescMesDesc(Long usuarioId);
}
