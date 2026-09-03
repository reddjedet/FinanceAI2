package com.financeai.transactions.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financeai.transactions.model.FrecuenciaAhorro;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class MlServiceClient {

    private final String mlServiceUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MlServiceClient(@Value("${app.services.ml.url:http://ml-service:8000}") String mlServiceUrl) {
        this.mlServiceUrl = mlServiceUrl.endsWith("/") ? mlServiceUrl.substring(0, mlServiceUrl.length() - 1) : mlServiceUrl;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String clasificarTransaccion(String descripcion) {
        try {
            Map<String, String> payload = Map.of("description", descripcion != null ? descripcion : "");
            String jsonBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(mlServiceUrl + "/api/v1/ml/classify-transaction"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(5))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                if (root.has("category")) {
                    return root.get("category").asText();
                }
            } else {
                System.err.println("ml-service classify error (" + response.statusCode() + "): " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Error al comunicarse con ml-service para clasificar transacción: " + e.getMessage());
        }
        return "Ocio";
    }

    public Map<String, Object> evaluarSaludFinanciera(BigDecimal ingreso, BigDecimal endeudamiento, FrecuenciaAhorro ahorro) {
        try {
            Map<String, Object> payload = Map.of(
                    "monthly_income", ingreso != null ? ingreso.doubleValue() : 0.0,
                    "debt_percentage", endeudamiento != null ? endeudamiento.doubleValue() : 0.0,
                    "saving_frequency", ahorro != null ? ahorro.name() : "NINGUNA"
            );
            String jsonBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(mlServiceUrl + "/api/v1/ml/financial-health"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(5))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), Map.class);
            } else {
                System.err.println("ml-service health error (" + response.statusCode() + "): " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Error al evaluar salud financiera con ml-service: " + e.getMessage());
        }
        return Map.of("financial_profile", "En observacion", "risk_level", "MEDIO");
    }

    public Map<String, Object> obtenerRecomendaciones(BigDecimal ingreso, BigDecimal endeudamiento, FrecuenciaAhorro ahorro, String perfil) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("monthly_income", ingreso != null ? ingreso.doubleValue() : 0.0);
            payload.put("debt_percentage", endeudamiento != null ? endeudamiento.doubleValue() : 0.0);
            payload.put("saving_frequency", ahorro != null ? ahorro.name() : "NINGUNA");
            if (perfil != null) {
                payload.put("financial_profile", perfil);
            }
            String jsonBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(mlServiceUrl + "/api/v1/ml/recommendations"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(5))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), Map.class);
            } else {
                System.err.println("ml-service recommendations error (" + response.statusCode() + "): " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Error al obtener recomendaciones de ml-service: " + e.getMessage());
        }
        return Map.of("summary", "Mantén un control prudente de tus gastos fijos y automatiza tu ahorro mensual.");
    }
}
