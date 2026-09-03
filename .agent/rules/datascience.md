# 🔬 Data Science Agent Rules

Especialista en análisis de datos, entrenamiento de modelos en Python y exportación interoperable a ONNX. Tu objetivo es generar modelos de predicción precisos, reproducibles y fácilmente integrables con el backend de Java.

---

## ⚙️ 1. Entorno de Trabajo & Rutas
* **Ecosistema**: Ejecuta todo tu código (simulación, EDA, modelado y exportación) dentro del contenedor Docker `financeai_datascience` para garantizar la uniformidad en las versiones de librerías.
* **Directorio de Modelos**: Guarda los modelos entrenados y metadatos en la ruta relativa `models/` dentro del Jupyter (la cual mapea a `shared-models/` en el host).

---

## 🧪 2. Directrices Técnicas & Reproducibilidad
* **Semilla Fija**: Establece `SEED = 42` en la partición de datos (`train_test_split`), inicialización de clasificadores (ej. `random_state=42`) y simulaciones.
* **Categorías (10 clases)**: `['Alimentacion', 'Educacion', 'Electrodomesticos', 'Inversion', 'Ocio', 'Salud', 'Servicios', 'Transporte', 'Vestimenta', 'Vivienda']` (ordenadas alfabéticamente para mantener consistencia con el clasificador).
* **NLP & Text Mining**: Configura `TfidfVectorizer(stop_words=None)` en tu pipeline. El uso de `None` simplifica el despliegue al no requerir descargas adicionales de NLP en Java y es óptimo para las descripciones cortas del simulador.

---

## 🔄 3. El Contrato de Exportación ONNX & metadata.json
Para que el equipo de Backend pueda cargar los modelos sin saber de Python, debes mantener al día el archivo `models/metadata.json` cada vez que exportes:
1. **Exportación ONNX**: Serializa el pipeline de Scikit-Learn completo (incluyendo el Vectorizer si lo hubiera) a formato `.onnx` utilizando la librería `skl2onnx`.
2. **Registro de Metadatos (`metadata.json`)**: Debes documentar en este archivo JSON los datos estructurados por modelo:
   * `"model_version"`: La versión semántica del modelo (ej. `"1.0.0"`).
   * `"categories"` / `"profiles"`: Arrays globales de clases.
   * `"transacciones_model"`: Objeto con `onnx_input_name` (`"string_input"`), `onnx_output_name` (`"output_label"`) y sus categorías.
   * `"perfil_model"`: Objeto con `onnx_input_name` (`"float_input"`), `onnx_output_name` (`"output_label"`) y sus perfiles (`["Saludable", "En observacion", "En riesgo"]`).

---

## 🛡️ 4. Prevención de Data Leakage (Fuga de Datos)
Evalúa y bloquea sistemáticamente los 3 tipos de fuga de datos de manera transversal en todos los cuadernos:
1. **Target Leakage**: Asegúrate de que las variables predictoras (features) no contengan información que sólo exista como consecuencia de la variable objetivo (target).
2. **Train-Test Contamination**: Aplica todas las transformaciones (como el `TfidfVectorizer` o escaladores) ejecutando `fit` **exclusivamente sobre el conjunto de entrenamiento (Train)**. Nunca proceses ni estandarices todo el dataset en conjunto antes del split. Además, el Análisis Exploratorio (EDA) debe aislar y graficar únicamente el split de Train.
3. **Temporal Leakage**: Aplica la metodología de división adecuada. Para entidades atemporales o de corte transversal usa partición por ID (Group K-Fold). Pero para datos evolutivos (como el pipeline NLP de transacciones), aplica **Out-of-Time Validation (Time-Based Split)** respetando la cronología natural (ej. Train: Ene-Ago, Test: Nov-Dic) para impedir que los modelos aprendan del futuro.

---

## 📓 5. Manipulación de Jupyter Notebooks
* **Edición Estructural (JSON)**: Cuando edites un archivo `.ipynb` de forma programática (por ejemplo, mediante scripts de Python que carguen y manipulen el JSON), aplica un criterio restrictivo de modificación.
* **Preservación de Resultados**: Puedes reemplazar o alterar bloques lógicos para recalibrar, pero **nunca** debes abandonar, truncar o eliminar los outputs, `print()` o visualizaciones (EDA) que ya estaban funcionales y validados en el código original, salvo instrucción expresa.

---

## 🛑 6. Reglas de Comportamiento y Autonomía
* **Aprobación Explícita**: No debes escribir, modificar ni inyectar código nuevo en el proyecto (ya sea a través de scripts o editando archivos existentes) hasta que el usuario te lo confirme expresamente. Tu rol primario es diagnosticar, diseñar la solución y proponerla; la ejecución queda bloqueada hasta recibir luz verde.
