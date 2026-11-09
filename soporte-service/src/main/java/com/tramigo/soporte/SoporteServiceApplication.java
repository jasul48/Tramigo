package com.tramigo.soporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SoporteServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SoporteServiceApplication.class, args);
    }
}
