package com.tramigo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/categorias")
    public String categorias() {
        return "categorias";
    }

    @GetMapping("/info_tramites")
    public String infoTramites() {
        return "info_tramites";
    }

    @GetMapping("/modulos")
    public String modulos() {
        return "modulos";
    }

    @GetMapping("/categorias_bancos")
    public String categoriasBancos() {
        return "categorias_bancos";
    }

    // Guías de trámites
    @GetMapping("/guia_ine")
    public String guiaIne() {
        return "guia_ine";
    }

    @GetMapping("/guia_acta")
    public String guiaActa() {
        return "guia_acta";
    }

    @GetMapping("/guia_curp")
    public String guiaCurp() {
        return "guia_curp";
    }

    @GetMapping("/guia_pasaporte")
    public String guiaPasaporte() {
        return "guia_pasaporte";
    }

    @GetMapping("/guia_inapam")
    public String guiaInapam() {
        return "guia_inapam";
    }

    @GetMapping("/guia_licencia")
    public String guiaLicencia() {
        return "guia_licencia";
    }

    @GetMapping("/guia_bbva")
    public String guiaBbva() {
        return "guia_bbva";
    }

    @GetMapping("/guia_citibanamex")
    public String guiaCitibanamex() {
        return "guia_citibanamex";
    }

    @GetMapping("/guia_santander")
    public String guiaSantander() {
        return "guia_santander";
    }

    @GetMapping("/guia_banco_azteca")
    public String guiaBancoAzteca() {
        return "guia_banco_azteca";
    }

    @GetMapping("/guia_banorte")
    public String guiaBanorte() {
        return "guia_banorte";
    }

    @GetMapping("/guia_rfc")
    public String guiaRfc() {
        return "guia_rfc";
    }

    @GetMapping("/guia_cedula")
    public String guiaCedula() {
        return "guia_cedula";
    }

    @GetMapping("/guia_visa")
    public String guiaVisa() {
        return "guia_visa";
    }

    @GetMapping("/guia_militar")
    public String guiaMilitar() {
        return "guia_militar";
    }
}
