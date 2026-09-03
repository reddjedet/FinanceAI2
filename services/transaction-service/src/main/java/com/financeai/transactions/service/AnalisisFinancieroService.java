package com.financeai.transactions.service;

import com.financeai.transactions.client.AuthServiceClient;
import com.financeai.transactions.client.MlServiceClient;
import com.financeai.transactions.dto.RespuestaAnalisisFinancieroDTO;
import com.financeai.transactions.dto.UserProfileDTO;
import com.financeai.transactions.model.AnalisisFinanciero;
import com.financeai.transactions.model.FrecuenciaAhorro;
import com.financeai.transactions.repository.AnalisisFinancieroRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalisisFinancieroService {

    private final AnalisisFinancieroRepository analisisRepository;
    private final PerfilFinancieroCalculatorService calculatorService;
    private final MlServiceClient mlServiceClient;
    private final AuthServiceClient authServiceClient;

    @Transactional
    public RespuestaAnalisisFinancieroDTO generarAnalisis(Long usuarioId, String authHeader) {
        UserProfileDTO usuario = authServiceClient.obtenerPerfilUsuario(usuarioId, authHeader);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId);
        }

        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);

        BigDecimal endeudamiento = calculatorService.calcularPorcentajeEndeudamiento(usuarioId, authHeader);
        FrecuenciaAhorro ahorro = calculatorService.calcularFrecuenciaAhorro(usuarioId);

        // Invocación al microservicio de ML
        Map<String, Object> evaluacion = mlServiceClient.evaluarSaludFinanciera(usuario.ingresoMensual(), endeudamiento, ahorro);
        String perfil = (String) evaluacion.getOrDefault("financial_profile", "En observacion");

        Map<String, Object> recomendacionData = mlServiceClient.obtenerRecomendaciones(usuario.ingresoMensual(), endeudamiento, ahorro, perfil);
        String recomendaciones = (String) recomendacionData.getOrDefault("summary", "Mantén un control presupuestario riguroso.");

        AnalisisFinanciero analisis = AnalisisFinanciero.builder()
                .usuarioId(usuarioId)
                .fechaAnalisis(LocalDateTime.now())
                .rangoInicio(inicioMes)
                .rangoFin(hoy)
                .perfilFinanciero(perfil)
                .porcentajeEndeudamiento(endeudamiento)
                .frecuenciaAhorro(ahorro)
                .recomendaciones(recomendaciones)
                .build();

        return new RespuestaAnalisisFinancieroDTO(analisisRepository.save(analisis));
    }

    @Transactional(readOnly = true)
    public List<RespuestaAnalisisFinancieroDTO> obtenerHistorial(Long usuarioId) {
        return analisisRepository.findByUsuarioIdOrderByFechaAnalisisDesc(usuarioId).stream()
                .map(RespuestaAnalisisFinancieroDTO::new)
                .toList();
    }
}
