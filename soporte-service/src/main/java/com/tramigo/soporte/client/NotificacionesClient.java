package com.tramigo.soporte.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

import java.util.Map;

/**
 * Cliente de notificaciones-service (descubierto via Eureka).
 * El aviso por correo es "mejor esfuerzo": si falla, el ticket ya quedó guardado.
 */
@Component
public class NotificacionesClient {

    private final RestClient restClient;
    private final boolean habilitado;

    public NotificacionesClient(@LoadBalanced RestClient.Builder builder,
                                @Value("${tramigo.notificaciones.base-url:http://notificaciones-service}") String baseUrl,
                                @Value("${tramigo.notificaciones.habilitado:true}") boolean habilitado) {
        this.restClient = builder.baseUrl(baseUrl).build();
        this.habilitado = habilitado;
    }

    /** @return true si notificaciones-service aceptó la solicitud. */
    public boolean notificarContacto(String nombre, String correo, String asunto, String mensaje) {
        if (!habilitado) {
            return false;
        }
        try {
            restClient.post()
                    .uri("/api/notificaciones/contacto")
                    .body(Map.of(
                            "nombre", nombre,
                            "correo", correo,
                            "asunto", asunto,
                            "mensaje", mensaje))
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            System.err.println("No se pudo notificar a notificaciones-service: " + e.getMessage());
            return false;
        }
    }
}
