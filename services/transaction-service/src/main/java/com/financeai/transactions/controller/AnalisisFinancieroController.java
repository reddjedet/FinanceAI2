package com.financeai.transactions.controller;

import com.financeai.transactions.dto.RespuestaAnalisisFinancieroDTO;
import com.financeai.transactions.service.AnalisisFinancieroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/financial-analysis")
@RequiredArgsConstructor
public class AnalisisFinancieroController {

    private final AnalisisFinancieroService analisisService;

    @PostMapping("/generate/{usuarioId}")
    public ResponseEntity<RespuestaAnalisisFinancieroDTO> generarAnalisis(
            @PathVariable Long usuarioId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.status(HttpStatus.CREATED).body(analisisService.generarAnalisis(usuarioId, authHeader));
    }

    @GetMapping("/history/{usuarioId}")
    public ResponseEntity<List<RespuestaAnalisisFinancieroDTO>> obtenerHistorial(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(analisisService.obtenerHistorial(usuarioId));
    }
}
