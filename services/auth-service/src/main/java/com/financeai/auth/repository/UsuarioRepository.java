package com.financeai.auth.repository;

import com.financeai.auth.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailAndActivoTrue(String email);
    Optional<Usuario> findByIdAndActivoTrue(Long id);
    List<Usuario> findAllByActivoTrue();
    boolean existsByEmail(String email);
}
