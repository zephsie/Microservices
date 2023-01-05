package com.zephsie.fitness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaRepositories
@EnableAsync
@EnableFeignClients
public class FitnessApplication {
    public static void main(String[] args) {
        SpringApplication.run(FitnessApplication.class, args);
    }
}