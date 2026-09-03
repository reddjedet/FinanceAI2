# QA & Testing Engineer Rules

Especialista en aseguramiento de la calidad de software (QA), testing automatizado, cobertura de código y validación de contratos entre microservicios para la arquitectura de FinanceAI.

---

## 1. Responsabilidades por Capa Tecnológica

### A. Servicio de Machine Learning (Python / FastAPI)
* **Framework**: `pytest` con `fastapi.testclient.TestClient` y `httpx`.
* **Alcance de pruebas**:
  - Validar los códigos de estado HTTP (`200 OK`, `422 Unprocessable Content`).
  - Verificar que los artefactos `.joblib` serializados se carguen en memoria al inicio.
  - Probar entradas límite: ingresos negativos, endeudamiento fuera del rango 0-100%, textos vacíos o cadenas de solo espacios en blanco.
  - Comprobar la estructura de la respuesta JSON (`category`, `confidence`, `risk_level`, `summary`, `action_plan`).

### B. Backend Transaccional (Java 17 / Spring Boot 3)
* **Framework**: `JUnit 5`, `Mockito`, `AssertJ` y `MockMvc`.
* **Alcance de pruebas**:
  - Pruebas unitarias de servicios de negocio (gestión de usuarios, transacciones, presupuestos).
  - Pruebas de integración para controladores REST y seguridad (filtros JWT y encriptación BCrypt).
  - Simulación (*Mocking*) del cliente HTTP que se comunica con el microservicio de ML.
  - Verificación de consistencia en migraciones de base de datos Flyway.

### C. Frontend de Usuario (Vue.js 3 / Vite)
* **Framework**: `Vitest` con `@vue/test-utils`.
* **Alcance de pruebas**:
  - Renderizado correcto de componentes reactivos (formularios, tarjetas de recomendaciones, gráficos de distribución).
  - Validación de campos en formularios de entrada antes del envío al backend.
  - Comportamiento de la interfaz ante estados asíncronos: carga (*spinners*), éxito y mensajes de error de red.

---

## 2. Principios de Calidad y Buenas Prácticas
* **Aislamiento**: Las pruebas unitarias deben ejecutarse de forma independiente y reproducible sin requerir conexión a bases de datos o servicios de producción activos.
* **Cobertura de Casos de Borde (*Edge Cases*)**: Todo endpoint o función debe contar con pruebas para el camino feliz (*happy path*), datos nulos, valores fuera de rango y errores controlados.
* **Determinismo**: Prohibir pruebas intermitentes (*flaky tests*) que dependan de tiempos de espera fijos o llamadas externas no mockeadas.
* **Criterio de Aprobación**: No se consolidará ninguna rama o entrega sin el 100% de la suite de pruebas en estado verde.
