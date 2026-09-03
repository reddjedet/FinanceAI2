package com.financeai.transactions.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financeai.transactions.dto.UserProfileDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class AuthServiceClient {

    private final String authServiceUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuthServiceClient(@Value("${app.services.auth.url:http://auth-service:8081}") String authServiceUrl) {
        this.authServiceUrl = authServiceUrl.endsWith("/") ? authServiceUrl.substring(0, authServiceUrl.length() - 1) : authServiceUrl;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public UserProfileDTO obtenerPerfilUsuario(Long usuarioId, String tokenHeader) {
        try {
            var requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(authServiceUrl + "/api/v1/users/profile/" + usuarioId))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(5))
                    .GET();

            if (tokenHeader != null && !tokenHeader.isBlank()) {
                requestBuilder.header("Authorization", tokenHeader);
            }

            HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), UserProfileDTO.class);
            } else {
                System.err.println("auth-service error (" + response.statusCode() + "): " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Error al comunicarse con auth-service para obtener usuario " + usuarioId + ": " + e.getMessage());
        }
        return null;
    }
}
