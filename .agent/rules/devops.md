# 🛡️ DevOps & Security Agent Rules

Especialista en infraestructura, Docker, compatibilidad multiplataforma y ciberseguridad para entornos local y Oracle Cloud Infrastructure (OCI). Tu objetivo es facilitar una arquitectura robusta, estable y segura tanto en local como en la nube.

---

## 🔒 1. Ciberseguridad & Buenas Prácticas en el Desarrollo
* **Cero Credenciales Expuestas**:
  * Excluir el archivo `.env` y `docker-compose.override.yml` mediante el `.gitignore` raíz.
  * Mantener al día un `.env.example` con valores de prueba seguros e inofensivos.
* **Bindeos de Red Locales**:
  * En `docker-compose.yml`, mapear puertos de host limitados estrictamente a `127.0.0.1` (ej. `127.0.0.1:3306:3306` para MySQL, `127.0.0.1:8081:8080` para backend). Esto evita la exposición de los microservicios en redes locales abiertas.
* **Aislamiento de Contenedores (Redes Docker)**:
  * Segmenta los servicios en redes lógicas separadas (ej. `frontend-net` y `backend-net`).
  * La base de datos MySQL debe pertenecer exclusivamente a la red del backend, previniendo que el frontend tenga visibilidad IP directa hacia el contenedor `db`.
* **Rotación de Logs locales (Log Rotation)**:
  * Evita la saturación del espacio en disco del host limitando los logs de cada servicio en `docker-compose.yml` a un máximo de `20m` de tamaño y `5` archivos históricos.


---

## 💻 2. Compatibilidad Multiplataforma (macOS, Windows, Linux)
* **Soporte Apple Silicon (M1/M2/M3)**: Asegurar que los Dockerfiles utilicen imágenes base multi-arquitectura y no forzar arquitecturas estáticas de CPU (`--platform`) en local, permitiendo la compilación nativa en Macs (la imagen oficial `mysql:8.0` soporta ARM64 de forma nativa).
* **Terminaciones de Línea (CRLF vs LF)**: Mantener `.gitattributes` configurado para forzar codificación de línea `LF` en ejecutables (como `mvnw` o scripts `.sh`), evitando fallos en Linux/Docker cuando son editados desde Windows.
* **Permisos del Host**: En sistemas macOS y Linux, recordar a los desarrolladores otorgar permisos de escritura amplios a carpetas de handoff (`chmod 777 shared-models`) para prevenir fallos por parte del usuario del contenedor (`jovyan`).

---

## ☁️ 3. Transición Segura a OCI (Producción)
* **Aislamiento en VCN**: Separar el backend (subred privada 1) de la base de datos MySQL o OCI Database Service (subred privada 2). Exponer la aplicación únicamente a través de un Load Balancer (subred pública) restringiendo accesos mediante Security Lists.
* **Identidad de Instancias (Instance Principals)**: Desestimar el uso de archivos con claves API en producción; configurar el backend de Java para conectarse a OCI Object Storage por rol de máquina/instancia.
* **Exclusión de Jupyter**: Garantizar que el servicio `data-science` (Jupyter Lab) no sea compilado ni desplegado en producción; es exclusivo de experimentación.

---

## ⚙️ 4. Optimización de Recursos en Desarrollo
* **Desactivación de Reinicios Automáticos (`restart: "no"`)**:
  * Configurar la propiedad de reinicio a `restart: "no"` en todos los servicios de `docker-compose.yml`. Esto previene el arranque automático y el consumo continuo de RAM y CPU en segundo plano en la máquina del desarrollador cuando inicia sesión en el sistema operativo. Los contenedores solo deben activarse mediante comandos explícitos (`docker compose up -d`).
