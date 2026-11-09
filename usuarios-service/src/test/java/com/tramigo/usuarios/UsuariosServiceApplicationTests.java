package com.tramigo.usuarios;

import com.tramigo.usuarios.model.Usuario;
import com.tramigo.usuarios.service.UsuarioService;
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

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class UsuariosServiceApplicationTests {

    @Autowired
    private UsuarioService usuarioService;

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
    void registraYAutenticaUsuarioConContrasenaCifrada() {
        String correo = UUID.randomUUID() + "@test.local";
        Usuario usuario = usuarioService.registrarUsuario(
                "Ada", "Lovelace", null, correo, "clave-segura");

        assertNotEquals("clave-segura", usuario.getContrasena());
        assertTrue(usuarioService.autenticar(correo, "clave-segura").isPresent());
        assertTrue(usuarioService.autenticar(correo, "incorrecta").isEmpty());
    }

    @Test
    void endpointLoginDevuelveVistaLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void endpointRegistroDevuelveVistaRegistro() throws Exception {
        mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(view().name("registro"));
    }

    @Test
    void endpointIndexDevuelveVistaIndex() throws Exception {
        mockMvc.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void endpointCrudDevuelveVistaCrud() throws Exception {
        mockMvc.perform(get("/crud"))
                .andExpect(status().isOk())
                .andExpect(view().name("crud"))
                .andExpect(model().attributeExists("personas"));
    }

    @Test
    void restApiAuthRegistroYLoginFunciona() throws Exception {
        String correo = "rest-" + UUID.randomUUID() + "@tramigo.test";
        String jsonRegistro = String.format("""
                {
                    "nombre": "Carlos",
                    "apellido": "Santana",
                    "correo": "%s",
                    "contrasena": "guitarra123"
                }
                """, correo);

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRegistro))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.usuario.correo").value(correo));

        String jsonLogin = String.format("""
                {
                    "correo": "%s",
                    "contrasena": "guitarra123"
                }
                """, correo);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.usuario.nombre").value("Carlos"));
    }

    @Test
    void restApiPersonasCrudFunciona() throws Exception {
        int idPrueba = (int) (System.currentTimeMillis() % 100000);
        String jsonPersona = String.format("""
                {
                    "id": %d,
                    "nombre": "Elena Poniatowska",
                    "edad": 92
                }
                """, idPrueba);

        mockMvc.perform(post("/api/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPersona))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(idPrueba))
                .andExpect(jsonPath("$.nombre").value("Elena Poniatowska"));

        mockMvc.perform(get("/api/personas/" + idPrueba))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.edad").value(92));
    }
}
