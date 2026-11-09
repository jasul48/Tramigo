package com.tramigo.notificaciones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class NotificacionesServiceApplicationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void sinSmtpConfiguradoLaNotificacionQuedaOmitida() throws Exception {
        String json = """
                {
                    "nombre": "Ana Lopez",
                    "correo": "ana@tramigo.test",
                    "asunto": "Duda sobre mi INE",
                    "mensaje": "Hola, necesito ayuda."
                }
                """;

        mockMvc.perform(post("/api/notificaciones/contacto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.notificacion.estado").value("OMITIDA"));

        mockMvc.perform(get("/api/notificaciones").param("estado", "omitida"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("CONTACTO"));
    }

    @Test
    void rechazaContactoSinCamposObligatorios() throws Exception {
        mockMvc.perform(post("/api/notificaciones/contacto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Solo nombre\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
