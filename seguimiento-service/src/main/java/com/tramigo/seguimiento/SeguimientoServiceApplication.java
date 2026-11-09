package com.tramigo.seguimiento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SeguimientoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeguimientoServiceApplication.class, args);
    }
}
