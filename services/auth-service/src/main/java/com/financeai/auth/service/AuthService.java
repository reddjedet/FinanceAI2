package com.financeai.auth.service;

import com.financeai.auth.dto.DatosLoginDTO;
import com.financeai.auth.dto.LoginRespuestaDTO;
import com.financeai.auth.model.Usuario;
import com.financeai.auth.repository.UsuarioRepository;
import com.financeai.auth.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public LoginRespuestaDTO login(DatosLoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(dto.email().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Credenciales incorrectas o usuario inactivo."));

        if (!passwordEncoder.matches(dto.password(), usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales incorrectas o usuario inactivo.");
        }

        String token = tokenService.generarToken(usuario);
        return new LoginRespuestaDTO(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getIngresoMensual()
        );
    }
}
