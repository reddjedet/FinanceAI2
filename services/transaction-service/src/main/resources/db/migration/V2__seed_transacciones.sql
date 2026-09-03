-- Inserción de transacciones modelo distribuidas en 10 categorías

-- Usuario 1: Ana García (Perfil Saludable - Alta inversión, bajo endeudamiento)
INSERT INTO transacciones (usuario_id, descripcion, monto, categoria, fecha, fecha_creacion) VALUES
(1, 'Alquiler Departamento Palermo', 650.00, 'Vivienda', '2026-08-02', '2026-08-02 10:00:00'),
(1, 'Edenor Factura Electricidad', 55.00, 'Servicios', '2026-08-04', '2026-08-04 11:30:00'),
(1, 'Metrogas Gas Natural', 40.00, 'Servicios', '2026-08-05', '2026-08-05 09:15:00'),
(1, 'Supermercado Coto Alimentos del Mes', 280.00, 'Alimentacion', '2026-08-07', '2026-08-07 16:20:00'),
(1, 'Lemon Cash Inversion Crypto BTC', 350.00, 'Inversion', '2026-08-08', '2026-08-08 14:00:00'),
(1, 'Bull Market Brokers Compra Cedears SPY', 400.00, 'Inversion', '2026-08-11', '2026-08-11 12:45:00'),
(1, 'Verduleria La Huerta', 45.00, 'Alimentacion', '2026-08-13', '2026-08-13 18:00:00'),
(1, 'Recarga Tarjeta SUBE Transporte', 30.00, 'Transporte', '2026-08-15', '2026-08-15 08:30:00'),
(1, 'Farmacity Medicamentos y Vitaminas', 65.00, 'Salud', '2026-08-18', '2026-08-18 19:10:00'),
(1, 'Restaurante Cena con Amigos', 120.00, 'Ocio', '2026-08-20', '2026-08-20 22:30:00'),
(1, 'InvertirOnline Aporte Fondo FCI', 300.00, 'Inversion', '2026-08-22', '2026-08-22 11:00:00'),
(1, 'Netflix y Spotify Suscripciones', 25.00, 'Ocio', '2026-08-25', '2026-08-25 00:05:00'),
(1, 'Indumentaria Deportiva Nike', 140.00, 'Vestimenta', '2026-08-27', '2026-08-27 17:40:00'),
(1, 'Platzi Curso Online Certificado', 80.00, 'Educacion', '2026-08-28', '2026-08-28 15:00:00');

-- Usuario 2: Juan Pérez (Perfil En Observación - Gastos moderados)
INSERT INTO transacciones (usuario_id, descripcion, monto, categoria, fecha, fecha_creacion) VALUES
(2, 'Pago Alquiler Depto 2 Ambientes', 780.00, 'Vivienda', '2026-08-02', '2026-08-02 09:30:00'),
(2, 'Expensas Consorcio Edificio', 190.00, 'Vivienda', '2026-08-05', '2026-08-05 10:15:00'),
(2, 'Edesur y Telecentro Internet', 110.00, 'Servicios', '2026-08-06', '2026-08-06 14:00:00'),
(2, 'Carrefour Express Compras Semanales', 220.00, 'Alimentacion', '2026-08-08', '2026-08-08 19:20:00'),
(2, 'Estacion YPF Carga Nafta Super', 85.00, 'Transporte', '2026-08-11', '2026-08-11 08:45:00'),
(2, 'Cena Restaurante y Salida Bar', 140.00, 'Ocio', '2026-08-14', '2026-08-14 23:15:00'),
(2, 'Zara Camisa y Pantalon', 180.00, 'Vestimenta', '2026-08-18', '2026-08-18 16:30:00'),
(2, 'Supermercado Dia Alimentos', 160.00, 'Alimentacion', '2026-08-21', '2026-08-21 18:50:00'),
(2, 'Plazo Fijo Banco Santander', 100.00, 'Inversion', '2026-08-25', '2026-08-25 10:00:00'),
(2, 'Farmacia Medicamentos', 45.00, 'Salud', '2026-08-27', '2026-08-27 12:00:00');

-- Usuario 3: Sofía Rodríguez (Perfil En Riesgo - Alto endeudamiento, sin ahorro)
INSERT INTO transacciones (usuario_id, descripcion, monto, categoria, fecha, fecha_creacion) VALUES
(3, 'Alquiler Monoambiente Belgrano', 720.00, 'Vivienda', '2026-08-02', '2026-08-02 09:00:00'),
(3, 'Expensas Consorcio con ABL', 230.00, 'Vivienda', '2026-08-04', '2026-08-04 11:00:00'),
(3, 'Pago Minimo Tarjeta Visa Servicios', 280.00, 'Servicios', '2026-08-06', '2026-08-06 15:30:00'),
(3, 'Compras Supermercado Disco', 240.00, 'Alimentacion', '2026-08-09', '2026-08-09 18:20:00'),
(3, 'Cuota Tarjeta Ropa Shopping', 190.00, 'Vestimenta', '2026-08-12', '2026-08-12 17:00:00'),
(3, 'Salidas Boliche y Cerveceria', 160.00, 'Ocio', '2026-08-16', '2026-08-16 02:00:00'),
(3, 'Viajes Uber y Cabify Fines de Semana', 95.00, 'Transporte', '2026-08-20', '2026-08-20 20:30:00'),
(3, 'Farmacia Analgesicos', 35.00, 'Salud', '2026-08-24', '2026-08-24 13:10:00');

-- Resúmenes mensuales históricos (Junio y Julio 2026)
INSERT INTO resumenes_mensuales (usuario_id, mes, anio, ingreso_fijo, total_gastado, sobrante_final, fecha_cierre) VALUES
(1, 6, 2026, 4500.00, 2450.00, 2050.00, '2026-06-30 23:59:59'),
(1, 7, 2026, 4500.00, 2580.00, 1920.00, '2026-07-31 23:59:59'),
(2, 6, 2026, 2400.00, 1950.00, 450.00, '2026-06-30 23:59:59'),
(2, 7, 2026, 2400.00, 2100.00, 300.00, '2026-07-31 23:59:59'),
(3, 6, 2026, 1600.00, 1850.00, -250.00, '2026-06-30 23:59:59'),
(3, 7, 2026, 1600.00, 1920.00, -320.00, '2026-07-31 23:59:59');

-- Análisis financieros históricos
INSERT INTO analisis_financiero (usuario_id, fecha_analisis, rango_inicio, rango_fin, perfil_financiero, porcentaje_endeudamiento, frecuencia_ahorro, recomendaciones) VALUES
(1, '2026-07-31 20:00:00', '2026-07-01', '2026-07-31', 'Saludable', 16.50, 'ALTA', 'Tus gastos fijos y deudas se encuentran en un nivel muy saludable y controlado. Excelente hábito de inversión sistemática.'),
(2, '2026-07-31 20:00:00', '2026-07-01', '2026-07-31', 'En observacion', 45.00, 'MEDIA', 'Tus gastos fijos están en un rango moderado. Trata de mantener mayor constancia en tus aportes mensuales de ahorro.'),
(3, '2026-07-31 20:00:00', '2026-07-01', '2026-07-31', 'En riesgo', 76.50, 'NINGUNA', 'Alerta: Tus gastos fijos superan el límite de seguridad. Prioriza renegociar servicios y liquidar compromisos de tarjeta de crédito.');
