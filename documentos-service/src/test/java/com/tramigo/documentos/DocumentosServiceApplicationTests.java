package com.tramigo.documentos;

import com.tramigo.documentos.model.Documento;
import com.tramigo.documentos.repository.DocumentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class DocumentosServiceApplicationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DocumentoRepository documentoRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void subeListaDescargaYEliminaDocumento() throws Exception {
        String usuarioId = UUID.randomUUID().toString();
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo", "acta.pdf", "application/pdf", "contenido-de-prueba".getBytes());

        mockMvc.perform(multipart("/api/documentos")
                        .file(archivo)
                        .param("usuarioId", usuarioId)
                        .param("nombre", "Mi acta"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.documento.nombrePersonalizado").value("Mi acta"))
                .andExpect(jsonPath("$.documento.tipoArchivo").value("pdf"));

        mockMvc.perform(get("/api/documentos/usuario/" + usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        List<Documento> docs = documentoRepository.findByUsuarioIdOrderByFechaSubidaDesc(usuarioId);
        assertEquals(1, docs.size());
        Integer id = docs.get(0).getDocumentoId();

        mockMvc.perform(get("/api/documentos/" + id + "/archivo").param("usuarioId", usuarioId))
                .andExpect(status().isOk());

        // Otro usuario no puede ver ni borrar el documento
        String intruso = UUID.randomUUID().toString();
        mockMvc.perform(get("/api/documentos/" + id).param("usuarioId", intruso))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/documentos/" + id).param("usuarioId", intruso))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/documentos/" + id).param("usuarioId", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/documentos/usuario/" + usuarioId))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void rechazaTiposDeArchivoNoPermitidos() throws Exception {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo", "virus.exe", "application/octet-stream", "x".getBytes());

        mockMvc.perform(multipart("/api/documentos")
                        .file(archivo)
                        .param("usuarioId", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
