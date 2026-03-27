package com.seveneleven.quantity_measurement_app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.seveneleven.quantity_measurement_app.repository")
public class JpaConfig {
}