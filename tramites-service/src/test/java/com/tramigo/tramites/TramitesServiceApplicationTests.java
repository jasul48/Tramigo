package com.tramigo.tramites;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class TramitesServiceApplicationTests {

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
    void catalogoInicialTiene11TramitesYLosBancosEnSuGrupo() throws Exception {
        mockMvc.perform(get("/api/tramites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(11))
                .andExpect(jsonPath("$[0].slug").value("ine"));

        mockMvc.perform(get("/api/tramites").param("grupo", "cuenta-bancaria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].slug").value("bbva"));
    }

    @Test
    void filtraPorCategoriaYBusquedaSinImportarAcentos() throws Exception {
        mockMvc.perform(get("/api/tramites").param("categoria", "viajes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/tramites").param("q", "cedula"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("cedula"));
    }

    @Test
    void buscaPorSlugYDevuelve404SiNoExiste() throws Exception {
        mockMvc.perform(get("/api/tramites/curp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("CURP"));

        mockMvc.perform(get("/api/tramites/no-existe"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crudDeTramiteNuevo() throws Exception {
        String slug = "prueba-" + UUID.randomUUID().toString().substring(0, 8);
        String json = String.format("""
                {
                    "slug": "%s",
                    "nombre": "Trámite de prueba",
                    "categoria": "Pruebas",
                    "descripcion": "Solo para tests"
                }
                """, slug);

        mockMvc.perform(post("/api/tramites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value(slug));

        mockMvc.perform(post("/api/tramites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());

        mockMvc.perform(put("/api/tramites/" + slug)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"Descripcion actualizada\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Descripcion actualizada"));

        mockMvc.perform(delete("/api/tramites/" + slug))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tramites/" + slug))
                .andExpect(status().isNotFound());
    }
}
