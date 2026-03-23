package com.matheus.tookYourMedicine.config;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

  @Bean(initMethod = "migrate")
  public Flyway flyway(DataSource dataSource) {
    return Flyway.configure()
        .dataSource(dataSource)
        .schemas("medicine")
        .defaultSchema("medicine")
        .locations("classpath:db/migration")
        .createSchemas(true)
        .load();
  }
}
