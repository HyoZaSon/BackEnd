package com.help.hyozason_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.help.hyozason_backend.entity")
public class HyozasonBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HyozasonBackendApplication.class, args);

    }

}
