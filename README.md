# FinanceAI - Plataforma Inteligente de Gestion Financiera

FinanceAI es una solucion integral para la gestion, autoclasificacion y analisis de salud financiera personal. Este repositorio contiene la version individual desarrollada como propuesta arquitectonica alternativa al proyecto grupal del hackaton.

---

## Contexto y Decision Arquitectonica

Durante el desarrollo grupal original se implemento la inferencia de Machine Learning embebida directamente en el backend de Java mediante el runtime de ONNX.

Para esta version individual, el objetivo principal fue encontrar el **punto optimo de equilibrio** entre simplicidad operacional, mantenibilidad y desacoplamiento, optando por una **arquitectura orientada a microservicios**:

1. **Microservicio dedicado de inferencia (Python + FastAPI)**: Permite explotar de forma nativa el ecosistema de Data Science (Scikit-Learn, Pandas, Joblib) con APIs asincronas de baja latencia, facilitando el reentrenamiento continuo y el versionado de artefactos sin requerir recompilar ni empaquetar binarios nativos en Java.
2. **Microservicios de negocio desacoplados (Java + Spring Boot 3)**: Separacion estricta de dominios entre autenticacion/usuarios (`auth-service`) y finanzas/transacciones (`transaction-service`), cada uno con su propio esquema de base de datos.
3. **Punto de entrada unico (Nginx API Gateway)**: Centraliza el enrutamiento, gestion de CORS y distribucion de trafico hacia los servicios internos.
4. **Despliegue homogeneo (Docker Compose)**: Orquestacion contenerizada reproducible en cualquier entorno (Linux, macOS, Windows).

---

## Objetivos y Consignas del Hackaton

El sistema cubre de forma automatizada los tres pilares solicitados:

1. **Clasificacion inteligente de transacciones**: Procesamiento de descripciones en texto libre mediante tecnicas de NLP (TF-IDF y clasificadores lineales/probabilisticos) para categorizar gastos e ingresos en las 10 categorias oficiales.
2. **Perfilado de salud financiera**: Evaluacion del estado del usuario (*Saludable*, *En observacion*, *En riesgo*) considerando nivel de ingresos, volumen de gastos y ratio de endeudamiento.
3. **Motor de recomendaciones personalizadas**: Generacion dinamica de sugerencias accionables de ahorro y optimizacion de consumo en funcion de la distribucion del gasto.

---

## Arquitectura del Sistema

```text
                      +-----------------------------+
                      |        Cliente Web          |
                      |   (Vue 3 + Vite / SPA)      |
                      +--------------+--------------+
                                     |
                                     v
                      +--------------+--------------+
                      |         API Gateway         |
                      |        (Nginx :8080)        |
                      +---+----------+----------+---+
                          |          |          |
         +----------------+          |          +----------------+
         |                           |                           |
         v                           v                           v
+-----------------+         +-----------------+         +-----------------+
|   auth-service  |         | transaction-    |         |    ml-service   |
| (Spring Boot)   |         |    service      |         |    (FastAPI)    |
|  Puerto :8081   |         |  (Spring Boot)  |         |  Puerto :8000   |
+--------+--------+         |  Puerto :8082   |         +--------+--------+
         |                  +--------+--------+                  |
         |                           |                           |
         |                           +───── Solicitud HTTP ─────>| (Inferencia NLP)
         v                           v                           v
[ MySQL: auth_db ]          [ MySQL: tx_db ]            [ Modelos Serializados ]
```

---

## Componentes y Tecnologias

| Servicio | Tecnologia | Puerto Host | Responsabilidad |
| :--- | :--- | :--- | :--- |
| **`gateway`** | Nginx | `8080` | Enrutador inverso, balanceo, CORS y punto de entrada unico. |
| **`auth-service`** | Java 17 / Spring Boot 3 | `8081` | Registro, autenticacion segura (BCrypt), JWT e historial de sueldo. |
| **`transaction-service`** | Java 17 / Spring Boot 3 | `8082` | Gestion de transacciones, saldos, resumenes y orquestacion de analisis. |
| **`ml-service`** | Python 3.11 / FastAPI | `8000` | Inferencia NLP (10 categorias), evaluacion de perfil y recomendaciones. |
| **`frontend`** | Vue 3 (Composition API) | `3000` | Panel de control interactivo para el usuario. |
| **`db`** | MySQL 8.0 | `3307` | Base de datos relacional con esquemas aislados y migraciones Flyway. |

---

## Estructura del Repositorio

```text
├── database/                # Script inicial de aprovisionamiento de esquemas SQL
├── frontend/                # Aplicacion cliente en Vue 3
├── gateway/                 # Configuracion de Nginx como API Gateway
├── notebooks/               # Cuadernos de experimentacion, simulacion y analisis ML
├── services/
│   ├── auth-service/        # Microservicio de registro, login y usuarios (Java)
│   ├── ml-service/          # Microservicio de inferencia y clasificacion (Python)
│   └── transaction-service/ # Microservicio de transacciones y resumenes (Java)
├── docker-compose.yml       # Orquestador multi-contenedor del sistema
├── .env.example             # Plantilla de variables de entorno
└── README.md                # Documentacion principal
```

---

## Guia de Puesta en Marcha

### Requisitos previos
* Docker y Docker Compose instalados.
* Git.

### 1. Clonar el repositorio y configurar variables de entorno
```bash
git clone <URL_DEL_REPOSITORIO>
cd <DIRECTORIO_DEL_PROYECTO>
cp .env.example .env
```

### 2. Iniciar el entorno completo
```bash
docker compose up -d --build
```

### 3. Verificar el estado de los servicios
```bash
docker compose ps
```

### 4. Puntos de acceso disponibles
* **Interfaz Web (Frontend)**: `http://localhost:8080` (a traves del Gateway) o `http://localhost:3000` (directo).
* **Documentacion interactiva de API ML (Swagger)**: `http://localhost:8080/api/v1/ml/docs`
* **API Gateway (Enrutador)**: `http://localhost:8080/api/v1/`

---

## Credenciales de Prueba para Demostracion

Para agilizar las pruebas en entornos locales, la base de datos se inicializa con usuarios semilla preconfigurados:

| Usuario | Email | Contraseña |
| :--- | :--- | :--- |
| Ana Garcia | `ana.garcia@financeai.com` | `PasswordSeguro123!` |
| Juan Perez | `juan.perez@financeai.com` | `PasswordSeguro123!` |
| Sofia Rodriguez | `sofia.rodriguez@financeai.com` | `PasswordSeguro123!` |

