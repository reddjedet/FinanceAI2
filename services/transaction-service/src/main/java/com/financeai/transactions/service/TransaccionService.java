package com.financeai.transactions.service;

import com.financeai.transactions.client.AuthServiceClient;
import com.financeai.transactions.client.MlServiceClient;
import com.financeai.transactions.dto.*;
import com.financeai.transactions.model.ResumenMensual;
import com.financeai.transactions.model.Transaccion;
import com.financeai.transactions.repository.ResumenMensualRepository;
import com.financeai.transactions.repository.TransaccionRepository;
import com.financeai.transactions.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final ResumenMensualRepository resumenMensualRepository;
    private final MlServiceClient mlServiceClient;
    private final AuthServiceClient authServiceClient;

    @Transactional
    public RespuestaTransaccionDTO crearTransaccion(IngresarTransaccionDTO dto, String authHeader) {
        SecurityUtils.validateUserOwnership(dto.idUsuario());
        UserProfileDTO usuario = authServiceClient.obtenerPerfilUsuario(dto.idUsuario(), authHeader);
        if (usuario == null || Boolean.FALSE.equals(usuario.activo())) {
            throw new ValidationException("Usuario no válido o inactivo.");
        }

        // Categorización inteligente con ml-service si no se especificó o se dejó vacía
        String categoria = dto.categoria();
        if (categoria == null || categoria.isBlank() || categoria.equalsIgnoreCase("Auto")) {
            categoria = mlServiceClient.clasificarTransaccion(dto.descripcion());
        }

        Transaccion transaccion = Transaccion.builder()
                .usuarioId(dto.idUsuario())
                .descripcion(dto.descripcion().trim())
                .monto(dto.monto())
                .categoria(categoria)
                .fecha(dto.fecha())
                .build();

        return new RespuestaTransaccionDTO(transaccionRepository.save(transaccion));
    }

    @Transactional(readOnly = true)
    public List<RespuestaTransaccionDTO> obtenerTransaccionesPorRango(Long usuarioId, LocalDate desde, LocalDate hasta) {
        SecurityUtils.validateUserOwnership(usuarioId);
        if (desde != null && hasta != null) {
            return transaccionRepository.findByUsuarioIdAndFechaBetweenOrderByFechaDesc(usuarioId, desde, hasta).stream()
                    .map(RespuestaTransaccionDTO::new)
                    .toList();
        }
        return transaccionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId).stream()
                .map(RespuestaTransaccionDTO::new)
                .toList();
    }

    @Transactional
    public RespuestaTransaccionDTO actualizarTransaccion(Long id, ActualizarTransaccionDTO dto) {
        Transaccion t = transaccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transacción no encontrada con ID: " + id));
        SecurityUtils.validateUserOwnership(t.getUsuarioId());

        if (dto.descripcion() != null && !dto.descripcion().isBlank()) {
            t.setDescripcion(dto.descripcion().trim());
        }
        if (dto.monto() != null) {
            t.setMonto(dto.monto());
        }
        if (dto.categoria() != null && !dto.categoria().isBlank()) {
            t.setCategoria(dto.categoria().trim());
        }
        if (dto.fecha() != null) {
            t.setFecha(dto.fecha());
        }

        return new RespuestaTransaccionDTO(transaccionRepository.save(t));
    }

    @Transactional
    public void eliminarTransaccion(Long id) {
        Transaccion t = transaccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transacción no encontrada con ID: " + id));
        SecurityUtils.validateUserOwnership(t.getUsuarioId());
        transaccionRepository.delete(t);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularSaldoDisponible(Long usuarioId, int mes, int anio, String authHeader) {
        SecurityUtils.validateUserOwnership(usuarioId);
        UserProfileDTO usuario = authServiceClient.obtenerPerfilUsuario(usuarioId, authHeader);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario no encontrado.");
        }

        BigDecimal sueldoBase = usuario.ingresoMensual() != null ? usuario.ingresoMensual() : BigDecimal.ZERO;

        int mesAnterior = (mes == 1) ? 12 : mes - 1;
        int anioAnterior = (mes == 1) ? anio - 1 : anio;

        BigDecimal sobranteMesAnterior = resumenMensualRepository
                .findByUsuarioIdAndAnioAndMes(usuarioId, anioAnterior, mesAnterior)
                .map(ResumenMensual::getSobranteFinal)
                .orElse(BigDecimal.ZERO);

        BigDecimal gastadoEnElMes = transaccionRepository.obtenerTotalGastadoEnMes(usuarioId, mes, anio);
        if (gastadoEnElMes == null) {
            gastadoEnElMes = BigDecimal.ZERO;
        }

        return sueldoBase.add(sobranteMesAnterior).subtract(gastadoEnElMes);
    }
}
