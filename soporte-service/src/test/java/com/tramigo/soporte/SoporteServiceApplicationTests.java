package com.tramigo.soporte;

import com.tramigo.soporte.repository.SoporteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class SoporteServiceApplicationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SoporteRepository soporteRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void creaTicketLoListaYCambiaSuEstado() throws Exception {
        String correo = "soporte-" + UUID.randomUUID() + "@tramigo.test";
        String json = String.format("""
                {
                    "nombre": "Ana Lopez",
                    "correo": "%s",
                    "asunto": "No puedo subir mi INE",
                    "mensaje": "Me marca error al subir el archivo."
                }
                """, correo);

        mockMvc.perform(post("/api/soporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.ticket.estado").value("Pendiente"))
                .andExpect(jsonPath("$.ticket.correo").value(correo));

        mockMvc.perform(get("/api/soporte/correo").param("correo", correo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        int id = soporteRepository.findByCorreoIgnoreCaseOrderByFechaCreacionDesc(correo).get(0).getTicketId();

        mockMvc.perform(put("/api/soporte/" + id + "/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"Resuelto\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticket.estado").value("Resuelto"));

        mockMvc.perform(put("/api/soporte/" + id + "/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"Inventado\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rechazaTicketSinCamposObligatorios() throws Exception {
        mockMvc.perform(post("/api/soporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Solo nombre\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void ticketInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/api/soporte/99999999"))
                .andExpect(status().isNotFound());
    }
}
