-- Inserción de usuarios semilla para desarrollo y demostración (Password: PasswordSeguro123!)
INSERT IGNORE INTO usuarios (id, nombre, email, password, ingreso_mensual, fecha_creacion, activo) VALUES
(1, 'Ana Garcia', 'ana.garcia@financeai.com', '$2a$10$AIlUC6U6n43UxbOE5.sk5u876udlVtf8Q6dUblB8NWOxSl8uiyNI2', 4500.00, '2026-05-01 09:00:00', TRUE),
(2, 'Juan Perez', 'juan.perez@financeai.com', '$2a$10$AIlUC6U6n43UxbOE5.sk5u876udlVtf8Q6dUblB8NWOxSl8uiyNI2', 2400.00, '2026-05-01 10:30:00', TRUE),
(3, 'Sofia Rodriguez', 'sofia.rodriguez@financeai.com', '$2a$10$AIlUC6U6n43UxbOE5.sk5u876udlVtf8Q6dUblB8NWOxSl8uiyNI2', 1600.00, '2026-05-01 11:15:00', TRUE);

INSERT IGNORE INTO historial_sueldo (id, usuario_id, sueldo_anterior, sueldo_nuevo, fecha_modificacion) VALUES
(1, 1, 4000.00, 4500.00, '2026-06-01 08:00:00'),
(2, 2, 2200.00, 2400.00, '2026-06-01 08:00:00'),
(3, 3, 1500.00, 1600.00, '2026-06-01 08:00:00');
