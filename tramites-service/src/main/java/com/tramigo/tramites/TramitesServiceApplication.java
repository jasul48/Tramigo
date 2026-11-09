package com.tramigo.tramites;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TramitesServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TramitesServiceApplication.class, args);
    }
}
