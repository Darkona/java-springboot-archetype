package com.archetype;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.archetype.clients")
@EnableMongoRepositories
@EnableJpaRepositories
public class ArchetypeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArchetypeApplication.class, args);
    }

}

