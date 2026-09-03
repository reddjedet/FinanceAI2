---
name: pedagogical-notebook-auditor
description: >-
  Audits, explains, and documents data science Jupyter notebooks and training
  scripts with pedagogical clarity and technical rigor. Use this skill when the
  user requests a deep educational breakdown, architectural justifications,
  statistical explanations, or script preparation for presenting machine learning
  code.
---

# Auditor pedagógico de ciencia de datos y notebooks

Este skill guía la auditoría técnica y didáctica de cuadernos de Jupyter (`.ipynb`), scripts de entrenamiento (`.py`) y artefactos de modelos en el proyecto. Su objetivo es generar documentación y notas estructuradas para que el desarrollador pueda explicar el flujo de trabajo con sus propias palabras, justificando el cómo y el por qué de cada decisión técnica.

---

## Procedimiento de auditoría

Al recibir la solicitud de analizar un notebook o script, sigue estos pasos:

1. **Lectura y extracción**:
   - Inspeccionar el archivo objetivo y sus salidas ejecutadas (gráficos, métricas impresas, estructuras de tensores).
   - Identificar las entradas de datos, transformaciones intermedias y artefactos exportados.

2. **Auditoría de integridad técnica y buenas prácticas**:
   - Verificar la ausencia de fuga de datos (*data leakage*): revisar que `fit` o `fit_transform` no se ejecuten sobre el conjunto de prueba o validación.
   - Comprobar la reproducibilidad (uso de semillas aleatorias fijas).
   - Evaluar la coherencia de las métricas frente al balance de clases.
   - Validar la exportación e interoperabilidad (por ejemplo, compatibilidad ONNX para el backend).

3. **Estructuración de la respuesta**:
   Generar el informe siguiendo la estructura obligatoria de salida.

---

## Estructura obligatoria de salida

### 1. Resumen conceptual del problema y dataset
- **Definición del problema**: Tipo de tarea de machine learning y objetivo de negocio.
- **Radiografía de los datos**: Variables de entrada, variable objetivo, tipos de datos y balance de clases.
- **Decisión de modelado**: Justificación de por qué este enfoque resuelve el problema planteado.

### 2. Diagrama de flujo del pipeline (Formato texto / ASCII)
Regla obligatoria: Evitar el uso de diagramas Mermaid. Emplear exclusivamente representaciones estructuradas en bloques de texto, listas secuenciales con sangría o diagramas esquemáticos en formato ASCII que detallen:
- Ingesta de datos -> División Train/Test -> Preprocesamiento/Vectorización -> Entrenamiento del estimador -> Validación -> Exportación (ONNX / metadatos).

### 3. Desglose didáctico por módulo con justificación matemática y estadística
Para cada sección lógica del código:
- **Propósito del bloque**: Qué hace en una frase concisa.
- **Detalle de la implementación**: Desglose de las líneas o hiperparámetros críticos.
- **Justificación técnica/matemática**: Explicación formal de la técnica empleada (por ejemplo, formulación de TF-IDF, regularización, balanceo de clases, funciones de pérdida).
- **Decisiones arquitectónicas**: Justificación de decisiones particulares de ingeniería (como inferencia en ONNX Runtime en lugar de un microservicio Python adicional).

### 4. Análisis de resultados, métricas y control de fuga de datos
- **Interpretación de métricas**: Análisis cuantitativo de los resultados obtenidos (Accuracy, Precision, Recall, F1-Score macro y ponderado).
- **Matriz de confusión y casos límite**: Identificación de clases con mayor tasa de error y su impacto operativo en el negocio.
- **Auditoría de Data Leakage**: Declaración explícita del estado de aislamiento de datos entre entrenamiento y evaluación.

### 5. Guion de apoyo para exposición en vivo (Talking Points)
- **Ideas clave**: 3 a 5 puntos esenciales a verbalizar durante la presentación.
- **Analogías didácticas**: Metáforas para explicar conceptos técnicos a la audiencia.
- **Preguntas frecuentes de la audiencia**: 2 o 3 preguntas técnicas complejas que podrían surgir y sus respuestas recomendadas.
