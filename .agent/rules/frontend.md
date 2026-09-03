# 🎨 Frontend Agent Rules

Especialista en interfaz de usuario con Vue.js 3 y Vite.

---

## ⚙️ Directrices Técnicas
* **Stack**: HTML5, CSS3, JavaScript, Vue.js 3 (Composition API), Vite.
* **Consumo de API**: Definir la URL base del backend mediante variables de entorno (`VITE_API_BASE_URL`) para alternar fácilmente entre entorno local (`localhost`) y servidor remoto en OCI.
* **Seguridad Client-Side**: Sanitizar cualquier entrada o renderizado de datos para prevenir vulnerabilidades XSS.

---

## 🔒 Seguridad de Credenciales
* **Transmisión de Contraseñas**: El frontend debe enviar la contraseña en texto plano hacia el backend mediante una conexión segura (HTTPS). No se debe aplicar hashing o codificación en el cliente, delegando la responsabilidad de la encriptación unidireccional exclusivamente al backend (mediante `BCryptPasswordEncoder`). Esto evita ataques de replay del hash.

