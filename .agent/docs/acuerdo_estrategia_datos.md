# Acuerdo de Estrategia de Datos y Lógica de Negocio

Este documento resume las decisiones arquitectónicas y metodológicas acordadas por el equipo de FinanceAI para el desarrollo de los modelos de datos y su integración con el backend.

---

## 1. Cambio en la Estrategia de Datos (Separación y Variedad)

* **Entidades Separadas:** Se procesan y modelan de manera independiente el dataset de usuarios (`usuarios.csv`) y el de transacciones (`transacciones.csv`). Ambas entidades se consolidan en el análisis final vinculándose mediante el identificador de usuario (`usuario_id`).
* **Diversificación por Países:** Con el fin de evitar el sobreajuste (*overfitting*) artificial en el clasificador de texto de transacciones (evitando métricas de F1/Accuracy perfectas de 1.00 causadas por listas cerradas y reducidas de comercios), el compañero data scientist compiló listados de nombres de comercios reales categorizados por país (Argentina, México, Brasil, Chile, Colombia, Perú, Ecuador, etc.).
* **Generación Aleatoria:** Las transacciones sintéticas se generan a partir de estas listas distribuidas geográficamente mediante selección aleatoria (`random.choice`), logrando un conjunto de datos mucho más variado, realista y desafiante para los modelos.

---

## 2. Alineación con el Backend (Lógica Mensual)

* **Estructura Temporal Mensual:** La lógica del análisis financiero y las recomendaciones del sistema se estructuran y agrupan por periodos mensuales para alinearse con el flujo implementado en el backend de Java.
* **Tipado de Fechas:** Las transacciones simuladas incorporan un campo de fecha formateado como tipo de dato `datetime`, permitiendo agrupamientos y filtros cronológicos por mes de forma nativa.
* **Ingreso Mensual como Parámetro de Entrada:** El ingreso mensual del usuario no se calcula de forma dinámica a partir del flujo de transacciones, sino que se suministra directamente como un dato de entrada (*input*). A partir de este dato y de las transacciones acumuladas en el mes, se calculan las siguientes métricas clave de salud financiera:
  * Proporciones de gastos por categoría.
  * Capacidad o tasa de ahorro.
  * Porcentaje de endeudamiento.

---

## 3. Código General y Automatización

* **EDA Parametrizable:** El código utilizado para el Análisis Exploratorio de Datos (EDA) y la generación de gráficos no debe ser estático ni depender de valores fijos.
* **Automatización de Visualizaciones:** Las funciones de visualización deben estructurarse de forma general y parametrizada, permitiendo al backend enviar cualquier conjunto de datos o usuario en el futuro y obtener sus métricas y gráficos correspondientes de manera automatizada.

---

## 4. Parámetros de Simulación Consolidados (Versión 2.0.0)

* **Volumen del Dataset:** Se configuran **1800 usuarios** y **240,000 transacciones** anuales (promedio de 133 transacciones por usuario al año, aprox. 11 mensuales) para simular un volumen real de Big Data.
* **Escala Salarial:** El rango de ingresos mensuales de los usuarios se establece de manera realista entre **$1500 y $6000 USD** para evitar distorsiones en la capacidad de pago.
* **Lógica de Endeudamiento (Promedio vs Dilución Anual):** Para evitar que el exceso de frecuencia de transacciones de vivienda (promedio 20 transacciones al año por el 15% de probabilidad) infle artificialmente los gastos fijos mensuales al sumarlas, el gasto mensual estimado se calcula como el promedio (`mean()`) del valor del ticket de Vivienda y Servicios. Esto erradica el colapso de varianza del 90% (clamping) en los perfiles.
* **Balanceo de Clases NLP:** Dada la naturaleza realista y desbalanceada de las categorías (ej. Servicios concentra el 20%, mientras Educación el 5%), el modelo NLP de transacciones en `3_training.ipynb` utiliza `class_weight='balanced'` en su Logistic Regression para resguardar la precisión de categorías minoritarias.
