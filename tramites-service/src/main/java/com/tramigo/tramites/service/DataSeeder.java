package com.tramigo.tramites.service;

import com.tramigo.tramites.model.Tramite;
import com.tramigo.tramites.repository.TramiteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Carga el catálogo inicial (el mismo que está hoy fijo en categorias.html y
 * categorias_bancos.html del monolito) cuando la tabla está vacía.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final String BANCOS = "cuenta-bancaria";

    private final TramiteRepository tramiteRepository;
    private final boolean seedEnabled;

    public DataSeeder(TramiteRepository tramiteRepository,
                      @Value("${tramigo.seed.enabled:true}") boolean seedEnabled) {
        this.tramiteRepository = tramiteRepository;
        this.seedEnabled = seedEnabled;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled || tramiteRepository.count() > 0) {
            return;
        }

        String ic = "/static/img/icons/";
        String bk = "/static/img/bancos/";
        String T = Tramite.TIPO_TRAMITE;
        String B = Tramite.TIPO_BANCO;

        List<Tramite> catalogo = List.of(
                new Tramite("ine", "Credencial para Votar (INE)", "Identidad",
                        "Guía para sacar tu INE por primera vez, renovarla por pérdida o vencimiento, o actualizar tu domicilio.",
                        ic + "ine.png", "/guia_ine", T, null, 1),
                new Tramite("acta", "Acta de Nacimiento", "Documentos",
                        "Aprende a descargar e imprimir tu copia certificada del acta de nacimiento en línea de forma oficial.",
                        ic + "acta.png", "/guia_acta", T, null, 2),
                new Tramite("curp", "CURP", "Documentos",
                        "Consulta, descarga e imprime tu Clave Única de Registro de Población (CURP) de manera gratuita.",
                        ic + "curp.png", "/guia_curp", T, null, 3),
                new Tramite(BANCOS, "Apertura de Cuenta Bancaria", "Finanzas",
                        "Qué documentos llevar y cómo abrir una cuenta básica en bancos populares como BBVA, Banamex o Santander.",
                        ic + "tarjetas.png", "/categorias_bancos", T, null, 4),
                new Tramite("pasaporte", "Pasaporte Mexicano (SRE)", "Viajes",
                        "Cómo agendar tu cita en la Secretaría de Relaciones Exteriores y cuánto cuesta el pasaporte vigente.",
                        ic + "passport.png", "/guia_pasaporte", T, null, 5),
                new Tramite("inapam", "Tarjeta INAPAM", "Adultos Mayores",
                        "Tramita tu credencial del INAPAM a los 60 años para obtener descuentos en medicinas, transporte y más.",
                        ic + "inapam.png", "/guia_inapam", T, null, 6),
                new Tramite("licencia", "Licencia de Conducir", "Vehicular",
                        "Requisitos generales para obtener o renovar tu licencia de automovilista particular en tu estado.",
                        ic + "licencia.png", "/guia_licencia", T, null, 7),
                new Tramite("rfc", "RFC (Inscripción al SAT)", "Documentos",
                        "Cómo obtener tu Registro Federal de Contribuyentes por primera vez para tu vida laboral.",
                        ic + "rfc.png", "/guia_rfc", T, null, 8),
                new Tramite("cedula", "Cédula Profesional", "Documentos",
                        "Trámite de la cédula electrónica oficial para egresados de licenciatura o posgrado.",
                        ic + "diploma.png", "/guia_cedula", T, null, 9),
                new Tramite("visa", "Visa Americana (B1/B2)", "Viajes",
                        "Pasos para llenar el formulario DS-160 y agendar tu cita en el consulado de EE.UU.",
                        ic + "passport.png", "/guia_visa", T, null, 10),
                new Tramite("militar", "Cartilla Militar", "Identidad",
                        "Requisitos para jóvenes de clase y remisos, incluyendo el sorteo y la liberación de la cartilla.",
                        ic + "militar.png", "/guia_militar", T, null, 11),

                // Bancos de la pantalla categorias_bancos.html
                new Tramite("bbva", "BBVA", "Finanzas",
                        "Líder en banca digital. Guía para cuenta de ahorros y nómina.",
                        bk + "bbva.png", "/guia_bbva", B, BANCOS, 1),
                new Tramite("citibanamex", "Citibanamex", "Finanzas",
                        "Tradición y respaldo. Requisitos para Cuenta Priority y básica.",
                        bk + "citibanamex.png", "/guia_citibanamex", B, BANCOS, 2),
                new Tramite("santander", "Santander", "Finanzas",
                        "Innovación constante. Pasos para abrir tu cuenta Santander LikeU.",
                        bk + "santander.png", "/guia_santander", B, BANCOS, 3),
                new Tramite("banco-azteca", "Banco Azteca", "Finanzas",
                        "Accesibilidad total. Guía para Guardadito y nómina.",
                        bk + "banco_azteca.png", "/guia_banco_azteca", B, BANCOS, 4),
                new Tramite("banorte", "Banorte", "Finanzas",
                        "El banco fuerte de México. Requisitos para Enlace Personal.",
                        bk + "banorte.png", "/guia_banorte", B, BANCOS, 5)
        );

        tramiteRepository.saveAll(catalogo);
        System.out.println("Catálogo inicial de trámites cargado: " + catalogo.size() + " registros.");
    }
}
