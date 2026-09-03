package com.financeai.auth.controller;

import com.financeai.auth.dto.*;
import com.financeai.auth.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UsuarioService usuarioService;

    @GetMapping("/profile/{id}")
    public ResponseEntity<RespuestaUsuarioDTO> obtenerPerfil(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPerfil(id));
    }

    @GetMapping
    public ResponseEntity<List<RespuestaUsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/salary")
    public ResponseEntity<HistorialSueldoDTO> actualizarSueldo(
            @PathVariable Long id,
            @RequestBody @Valid ActualizarSueldoDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarSueldo(id, dto));
    }

    @GetMapping("/{id}/salary-history")
    public ResponseEntity<List<HistorialSueldoDTO>> obtenerHistorialSueldo(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerHistorialSueldo(id));
    }
}
