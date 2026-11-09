package com.tramigo.documentos.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

/**
 * Cliente de usuarios-service (descubierto via Eureka).
 * Se usa para comprobar que el usuario dueño de un documento realmente existe.
 */
@Component
public class UsuariosClient {

    private final RestClient restClient;
    private final boolean validar;

    public UsuariosClient(@LoadBalanced RestClient.Builder builder,
                          @Value("${tramigo.usuarios.base-url:http://usuarios-service}") String baseUrl,
                          @Value("${tramigo.usuarios.validar:true}") boolean validar) {
        this.restClient = builder.baseUrl(baseUrl).build();
        this.validar = validar;
    }

    /**
     * @return true si el usuario existe; false si usuarios-service responde 404.
     * @throws UsuariosNoDisponibleException si usuarios-service no responde.
     */
    public boolean existeUsuario(String usuarioId) {
        if (!validar) {
            return true;
        }
        try {
            HttpStatusCode status = restClient.get()
                    .uri("/api/usuarios/{id}", usuarioId)
                    .exchange((request, response) -> response.getStatusCode());
            if (status.value() == 404) {
                return false;
            }
            if (status.is2xxSuccessful()) {
                return true;
            }
            throw new UsuariosNoDisponibleException("usuarios-service respondió con estado " + status.value());
        } catch (UsuariosNoDisponibleException e) {
            throw e;
        } catch (Exception e) {
            throw new UsuariosNoDisponibleException("No se pudo contactar a usuarios-service: " + e.getMessage());
        }
    }

    public static class UsuariosNoDisponibleException extends RuntimeException {
        public UsuariosNoDisponibleException(String message) {
            super(message);
        }
    }
}
