package com.legalnow.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = ApiApplicationTests.PostgresInitializer.class)
class ApiApplicationTests {

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("legalnow")
        .withUsername("legalnow")
        .withPassword("legalnow");

    static {
        if (isDockerAvailable()) {
            POSTGRES.start();
        }
    }

    @Autowired
    private ApiApplication application;

    @Test
    void contextLoads() {
        assumeTrue(isDockerAvailable(), "Docker is not available; skipping context-load test.");
        assertNotNull(application);
    }

    private static boolean isDockerAvailable() {
        try {
            DockerClientFactory.instance().client();
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    static class PostgresInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            if (!isDockerAvailable()) {
                return;
            }
            TestPropertyValues.of(
                "spring.datasource.url=" + POSTGRES.getJdbcUrl(),
                "spring.datasource.username=" + POSTGRES.getUsername(),
                "spring.datasource.password=" + POSTGRES.getPassword(),
                "spring.jpa.hibernate.ddl-auto=validate",
                "spring.flyway.enabled=true"
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}
