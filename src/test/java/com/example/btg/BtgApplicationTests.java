package com.example.btg;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class BtgApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:6.0"))
        .withReuse(true)
        .withExposedPorts(27017);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        if (mongoDBContainer.isRunning()) {
            String mongoUri = String.format("mongodb://%s:%d/btgdb", 
                mongoDBContainer.getHost(), 
                mongoDBContainer.getFirstMappedPort());
            registry.add("spring.data.mongodb.uri", () -> mongoUri);
        }
    }

    @Test
    void contextLoads() {
        // This test will be skipped if Docker is not available
        if (!mongoDBContainer.isRunning()) {
            System.out.println("Docker is not available. Skipping container-based tests.");
            System.out.println("To run tests with MongoDB, please install Docker and start Docker Desktop.");
            return;
        }
        System.out.println("MongoDB container is running at: " + 
            mongoDBContainer.getHost() + ":" + mongoDBContainer.getFirstMappedPort());
    }
}
