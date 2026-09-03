package com.financeai.transactions.service;

import com.financeai.transactions.client.AuthServiceClient;
import com.financeai.transactions.dto.UserProfileDTO;
import com.financeai.transactions.model.FrecuenciaAhorro;
import com.financeai.transactions.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PerfilFinancieroCalculatorService {

    private final TransaccionRepository transaccionRepository;
    private final AuthServiceClient authServiceClient;

    public BigDecimal calcularPorcentajeEndeudamiento(Long usuarioId, String tokenHeader) {
        UserProfileDTO usuario = authServiceClient.obtenerPerfilUsuario(usuarioId, tokenHeader);
        if (usuario == null || usuario.ingresoMensual() == null || usuario.ingresoMensual().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);

        BigDecimal gastosFijos = transaccionRepository.sumarGastosFijosPorRango(usuarioId, inicioMes, hoy);
        if (gastosFijos == null) {
            gastosFijos = BigDecimal.ZERO;
        }

        return gastosFijos.divide(usuario.ingresoMensual(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public FrecuenciaAhorro calcularFrecuenciaAhorro(Long usuarioId) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);

        long aportesInversion = transaccionRepository.contarTransaccionesInversionPorRango(usuarioId, inicioMes, hoy);

        if (aportesInversion >= 4) {
            return FrecuenciaAhorro.ALTA;
        } else if (aportesInversion >= 2) {
            return FrecuenciaAhorro.MEDIA;
        } else if (aportesInversion == 1) {
            return FrecuenciaAhorro.BAJA;
        } else {
            return FrecuenciaAhorro.NINGUNA;
        }
    }
}
