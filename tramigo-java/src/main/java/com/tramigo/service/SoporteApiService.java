package com.tramigo.service;

import com.tramigo.model.Soporte;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

/**
 * Cliente de la API de soporte. Mantiene la comunicación HTTP fuera del controlador MVC
 * y evita que la interfaz web escriba directamente en la base de datos del microservicio.
 */
@Service
public class SoporteApiService {
    private final RestClient restClient;

    public SoporteApiService(@Value("${tramigo.api.base-url:http://localhost:8090}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public void crearTicket(String nombre, String correo, String asunto, String mensaje) {
        Map<String, String> request = Map.of(
                "nombre", nombre.trim(),
                "correo", correo.trim(),
                "asunto", asunto.trim(),
                "mensaje", mensaje.trim()
        );

        restClient.post()
                .uri("/api/soporte")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public List<Soporte> listarTickets() {
        List<Soporte> tickets = restClient.get()
                .uri("/api/soporte")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Soporte>>() {});
        return tickets == null ? List.of() : tickets;
    }
}
