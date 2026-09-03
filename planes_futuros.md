# Planes de Mejora y Roadmap Futuro - FinanceAI

Este documento consolida las mejoras arquitectónicas, contables y de modelado de Machine Learning acordadas para implementarse en fases posteriores del proyecto.

---

## 1. Simulación y Generación de Datos

* **Escalado de Montos por Poder Adquisitivo**:
  - Incorporar un multiplicador de escala salarial (`factor_ingreso = ingreso_usuario / ingreso_base`) en el generador de transacciones.
  - Ajustar dinámicamente los rangos de gasto en categorías elásticas (Vivienda, Ocio, Inversión, Vestimenta) para que los montos nominales guarden proporción con el nivel salarial del usuario.
* **Ampliación del Vocabulario y Variedad de Comercios**:
  - Expandir el diccionario base a cientos de comercios y conceptos de consumo en español real (ej. `BODEGON DE BARRIO`, `MATERIAL ESTUDIO FOTOCOPIAS`, `COMBUSTIBLE DIESEL YPF`, `ADQUISICION BONOS SOBERANOS`).
  - Enriquecer los n-gramas de subpalabras en el vectorizador TF-IDF para robustecer el clasificador ante variedades léxicas regionales.
* **Evolución Salarial y Series Temporales**:
  - Modelar eventos periódicos de actualización de haberes (aumentos de sueldo o promociones) que disparen automáticamente el recalculo del perfil de salud financiera.

---

## 2. Lógica Financiera y Contable

* **Cálculo de Flujo de Caja Acumulativo (*Cash Flow*)**:
  - Implementar un motor de contabilidad mensual en el backend:
    $$\text{Saldo final}_t = \text{Saldo inicial} + \text{Ingresos}_t - \sum \text{Gastos}_t$$
  - Capitalizar el remanente positivo mensual como ahorro líquido o inversión acumulada, y trasladar los déficits mensuales a un incremento del endeudamiento con intereses.
* **Separación de Gastos Fijos vs. Variables (Regla 50/30/20)**:
  - Mapear las 10 categorías en dos grandes grupos presupuestarios:
    * **Gastos Fijos / Necesidades (50% objetivo)**: Vivienda, Servicios, Salud, Educación.
    * **Gastos Variables / Deseos (30% objetivo)**: Alimentación, Transporte, Vestimenta, Ocio, Electrodomésticos.
    * **Ahorro e Inversión (20% objetivo)**: Inversión.
  - Emitir alertas tempranas y planes de acción cuando los gastos fijos superen el 50% o los consumos discrecionales superen el 30% del ingreso neto.
* **Categoría Transaccional Formal: "Créditos y Deudas"**:
  - Incorporar una 11ª categoría transaccional para aislar los pagos de resúmenes de tarjeta y amortización de créditos.
  - Automatizar el cálculo directo del ratio **DTI (*Debt-to-Income*)** a partir del extracto bancario sin requerir declaración manual del usuario.

---

## 3. Microservicio de Inferencia y MLOps

* **Gestión de Incertidumbre y Baja Confianza (*Human-in-the-Loop*)**:
  - Establecer una regla de bifurcación de certeza en el backend para umbrales de predicción:
    * Si $\text{Confianza} \ge 65\% \implies$ Clasificación automática y definitiva.
    * Si $\text{Confianza} < 65\% \implies$ Marcar la transacción como `requiere_confirmacion = true` y mostrar una sugerencia con selector manual en la interfaz de Vue.
* **Versionado Semántico de Modelos Serializados**:
  - Formalizar un esquema de versionado para los artefactos exportados (ej. `transaction_classifier_v2.1.0.joblib`) junto con un archivo de metadatos (`metadata.json`) que registre la fecha de entrenamiento, métricas de F1-Score y hash de integridad para facilitar *rollbacks*.
* **Pipeline de Entrenamiento Desacoplado (*CLI / CI-CD*)**:
  - Migrar la ejecución de reentrenamiento de los cuadernos interactivos hacia un script de ejecución por línea de comandos (`python -m app.train`) integrado a un pipeline de integración continua.
