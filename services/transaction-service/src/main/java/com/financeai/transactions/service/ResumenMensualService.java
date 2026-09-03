package com.financeai.transactions.service;

import com.financeai.transactions.dto.RespuestaResumenMensualDTO;
import com.financeai.transactions.model.ResumenMensual;
import com.financeai.transactions.repository.ResumenMensualRepository;
import com.financeai.transactions.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumenMensualService {

    private final ResumenMensualRepository resumenMensualRepository;

    @Transactional(readOnly = true)
    public List<RespuestaResumenMensualDTO> obtenerResumenesPorUsuario(Long usuarioId) {
        SecurityUtils.validateUserOwnership(usuarioId);
        return resumenMensualRepository.findByUsuarioIdOrderByAnioDescMesDesc(usuarioId).stream()
                .map(RespuestaResumenMensualDTO::new)
                .toList();
    }
}
