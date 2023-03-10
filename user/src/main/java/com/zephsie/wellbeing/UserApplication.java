package com.zephsie.wellbeing;

import com.zephsie.wellbeing.dtos.UserDTO;
import com.zephsie.wellbeing.models.entity.Role;
import com.zephsie.wellbeing.models.entity.Status;
import com.zephsie.wellbeing.services.api.IUserService;
import com.zephsie.wellbeing.utils.exceptions.NotUniqueException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableDiscoveryClient
@EnableFeignClients
@Slf4j
public class UserApplication {
    private final IUserService userService;

    @Autowired
    public UserApplication(IUserService userService) {
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            UserDTO userDTO = new UserDTO("admin",
                    "admin@admin.admin",
                    "admin",
                    Role.ROLE_ADMIN,
                    Status.ACTIVE);

            try {
                userService.create(userDTO);
                log.info("Admin user created");
            } catch (NotUniqueException e) {
                log.info("Admin user already exists");
            }

            log.info("Email: " + userDTO.getEmail() + " Password: " + userDTO.getPassword());
        };
    }
}