# ☕ Backend Agent Rules

Especialista en desarrollo Java con Spring Boot, consumo de modelos ONNX y base de datos MySQL. Tu objetivo es escribir una API REST robusta, simple y fácil de consumir por el frontend.

---

## ⚙️ 1. Base de Datos & Persistencia (MySQL & Flyway)
* **Variables de Entorno**: Leer las credenciales de base de datos inyectadas por Docker Compose:
  * `spring.datasource.url=jdbc:mysql://${DB_HOST}/FinanceAI?createDatabaseIfNotExist=true`
  * `spring.datasource.username=${DB_USER_M}`
  * `spring.datasource.password=${DB_PASSWORD}`
* **Esquema de BD (Flyway)**: Gestionar la creación de tablas mediante scripts de migración de Flyway ubicados en `src/main/resources/db/migration/`. Utiliza la sintaxis compatible con MySQL 8.0 (ej. `auto_increment`).
* **Semillero de Datos (Seeder)**: Implementa una clase que implemente `CommandLineRunner` en Spring Boot o utiliza migraciones de Flyway (ej. `V2__insertar_datos_semilla.sql`) para insertar usuarios de prueba y categorías iniciales en la base de datos MySQL al arrancar.

---

## 🧠 2. Consumo de Modelos ONNX (Inferencia Local)
Para ejecutar los modelos de inteligencia artificial en Java sin depender de Python, utiliza la librería **ONNX Runtime**:
1. **Dependencia**: Asegúrate de tener `ai.onnxruntime:onnxruntime` en el `pom.xml`.
2. **Carga en Memoria**: 
   * Carga el archivo `.onnx` usando `OrtEnvironment` y `OrtSession`.
   * Lee el archivo `metadata.json` en `shared-models/` usando la librería Jackson para conocer dinámicamente los nombres de los tensores de entrada/salida y las categorías de clasificación.
3. **Inferencia (Predicción)**:
   * **Clasificador de Transacciones**: Toma la descripción del gasto, aplícale el mismo preprocesamiento que Data Science y pásalo como un tensor.
   * **Perfilado Financiero**: Crea un vector numérico con las variables del perfil del usuario (ingresos, deudas, ahorros) y pásalo a la sesión de ONNX.
4. **Manejo de Recursos**: Abre las sesiones de ONNX en un Singleton al arrancar la app y ciérralas adecuadamente para evitar fugas de memoria.

---

## 🔌 3. API REST & Errores
* **Endpoint Principal**: Implementa `POST /analisis-financiero` cumpliendo con la estructura de entrada/salida definida en el README principal.
* **Control de Excepciones**: Captura errores de base de datos o de inferencia de ONNX y devuelve códigos HTTP semánticos (ej. `400 Bad Request`, `500 Internal Server Error`) con un cuerpo JSON descriptivo para que el frontend pueda diagnosticar fallos fácilmente.

---

## ☁️ 4. Integración con OCI Object Storage
* **Propósito**: Consumir o almacenar recursos de forma remota en la nube.
* **Implementación**: Utilizar el SDK de OCI para Java para leer/escribir archivos (ej. modelos `.onnx` actualizados o datos de respaldo) desde el Object Storage Bucket (`OCI_STORAGE_BUCKET`) usando la configuración de autenticación adecuada para OCI.

---

## 💾 5. Buenas Prácticas de Persistencia y Lógica de Negocio
* **Transaccionalidad en la capa de Servicio**:
  * Anotar siempre los métodos de modificación y persistencia (guardar, actualizar, eliminar) con `@Transactional` a nivel de la clase `@Service`. Evitar delegar la transaccionalidad únicamente a la capa de `@RestController`, garantizando que la lógica persista de forma independiente al protocolo de entrada.
* **Integridad de Datos Financieros de Perfil**:
  * El ingreso mensual de un usuario debe tratarse como un parámetro estático de su perfil (solo lectura). No descontar de manera destructiva los montos de las transacciones directamente de este campo en la base de datos, ya que corrompe los análisis mensuales de gastos, capacidad de ahorro y endeudamiento planificados por Data Science.
* **Consultas de Colecciones Filtradas (Riesgo JPA/Hibernate)**:
  * Evitar el uso de `LEFT JOIN FETCH` combinado con filtros sobre la colección secundaria en la cláusula `WHERE` (ej. filtrar transacciones de un usuario por mes/año). Esto anula el `LEFT JOIN` y puede provocar que Hibernate trunque y corrompa la relación en la base de datos si el objeto principal es actualizado en la misma sesión.
* **Nombres de Propiedades y Clases**:
  * Asegurar la concordancia exacta en los nombres de las clases (ej. escribir `TransaccionService` con doble 'c') y de las propiedades de Spring (ej. `spring.datasource.driver-class-name` con la 'r' final).

---

## 🛡️ 6. Seguridad & Encriptación de Contraseñas
* **Uso de BCryptPasswordEncoder**: No almacenar bajo ninguna circunstancia contraseñas en texto plano en la base de datos MySQL. Se debe utilizar la clase `BCryptPasswordEncoder` de Spring Security para generar el hash unidireccional de las contraseñas antes de guardarlas en la base de datos.
* **Verificación de Credenciales**: Al autenticar un usuario, comparar la contraseña recibida con la contraseña almacenada usando el método `matches(rawPassword, encodedPassword)` del encoder de contraseñas.
* **Longitud del Campo de Contraseña**: Asegurar que en las tablas de base de datos el campo `password` tenga una longitud suficiente (por ejemplo, `VARCHAR(255)`) para almacenar el hash de BCrypt, el cual consta de 60 caracteres.


