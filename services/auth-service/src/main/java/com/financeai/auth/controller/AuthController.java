package com.financeai.auth.controller;

import com.financeai.auth.dto.DatosLoginDTO;
import com.financeai.auth.dto.IngresarUsuarioDTO;
import com.financeai.auth.dto.LoginRespuestaDTO;
import com.financeai.auth.dto.RespuestaUsuarioDTO;
import com.financeai.auth.security.TokenService;
import com.financeai.auth.service.AuthService;
import com.financeai.auth.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginRespuestaDTO> login(@RequestBody @Valid DatosLoginDTO datos) {
        LoginRespuestaDTO respuesta = authService.login(datos);
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/register")
    public ResponseEntity<RespuestaUsuarioDTO> register(@RequestBody @Valid IngresarUsuarioDTO datos) {
        RespuestaUsuarioDTO respuesta = usuarioService.registrarUsuario(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valid", false, "error", "Token ausente"));
        }
        try {
            String token = authHeader.substring(7);
            String email = tokenService.getSubject(token);
            Long userId = tokenService.getUserId(token);
            return ResponseEntity.ok(Map.of("valid", true, "email", email, "userId", userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valid", false, "error", e.getMessage()));
        }
    }
}
