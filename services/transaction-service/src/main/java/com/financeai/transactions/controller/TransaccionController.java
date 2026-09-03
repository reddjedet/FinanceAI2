package com.financeai.transactions.controller;

import com.financeai.transactions.dto.*;
import com.financeai.transactions.service.ResumenMensualService;
import com.financeai.transactions.service.TransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService transaccionService;
    private final ResumenMensualService resumenMensualService;

    @PostMapping
    public ResponseEntity<RespuestaTransaccionDTO> registrarTransaccion(
            @RequestBody @Valid IngresarTransaccionDTO dto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transaccionService.crearTransaccion(dto, authHeader));
    }

    @GetMapping
    public ResponseEntity<List<RespuestaTransaccionDTO>> listarTransacciones(
            @RequestParam Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(transaccionService.obtenerTransaccionesPorRango(usuarioId, desde, hasta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespuestaTransaccionDTO> actualizarTransaccion(
            @PathVariable Long id,
            @RequestBody ActualizarTransaccionDTO dto) {
        return ResponseEntity.ok(transaccionService.actualizarTransaccion(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTransaccion(@PathVariable Long id) {
        transaccionService.eliminarTransaccion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available-balance")
    public ResponseEntity<Map<String, BigDecimal>> obtenerSaldoDisponible(
            @RequestParam Long usuarioId,
            @RequestParam int mes,
            @RequestParam int anio,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        BigDecimal saldo = transaccionService.calcularSaldoDisponible(usuarioId, mes, anio, authHeader);
        return ResponseEntity.ok(Map.of("saldoDisponible", saldo));
    }

    @GetMapping("/summary/{usuarioId}")
    public ResponseEntity<List<RespuestaResumenMensualDTO>> obtenerResumenesMensuales(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(resumenMensualService.obtenerResumenesPorUsuario(usuarioId));
    }
}
