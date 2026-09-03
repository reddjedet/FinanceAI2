package com.financeai.auth.service;

import com.financeai.auth.dto.*;
import com.financeai.auth.model.HistorialSueldo;
import com.financeai.auth.model.Usuario;
import com.financeai.auth.repository.HistorialSueldoRepository;
import com.financeai.auth.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final HistorialSueldoRepository historialSueldoRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RespuestaUsuarioDTO registrarUsuario(IngresarUsuarioDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("El correo electrónico ya se encuentra registrado.");
        }

        Usuario usuario = Usuario.builder()
                .nombre(dto.nombre().trim())
                .email(dto.email().trim().toLowerCase())
                .password(passwordEncoder.encode(dto.password()))
                .ingresoMensual(dto.ingresoMensual())
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);

        // Registro inicial en historial de sueldo
        HistorialSueldo historial = HistorialSueldo.builder()
                .usuarioId(guardado.getId())
                .sueldoAnterior(dto.ingresoMensual())
                .sueldoNuevo(dto.ingresoMensual())
                .build();
        historialSueldoRepository.save(historial);

        return new RespuestaUsuarioDTO(guardado);
    }

    @Transactional(readOnly = true)
    public RespuestaUsuarioDTO obtenerPerfil(Long id) {
        Usuario usuario = usuarioRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        return new RespuestaUsuarioDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<RespuestaUsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .map(RespuestaUsuarioDTO::new)
                .toList();
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public HistorialSueldoDTO actualizarSueldo(Long usuarioId, ActualizarSueldoDTO dto) {
        Usuario usuario = usuarioRepository.findByIdAndActivoTrue(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        var sueldoAnterior = usuario.getIngresoMensual();
        usuario.setIngresoMensual(dto.nuevoSueldo());
        usuarioRepository.save(usuario);

        HistorialSueldo historial = HistorialSueldo.builder()
                .usuarioId(usuarioId)
                .sueldoAnterior(sueldoAnterior)
                .sueldoNuevo(dto.nuevoSueldo())
                .build();

        return new HistorialSueldoDTO(historialSueldoRepository.save(historial));
    }

    @Transactional(readOnly = true)
    public List<HistorialSueldoDTO> obtenerHistorialSueldo(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId);
        }
        return historialSueldoRepository.findByUsuarioIdOrderByFechaModificacionDesc(usuarioId).stream()
                .map(HistorialSueldoDTO::new)
                .toList();
    }
}
