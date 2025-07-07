package com.example.btg.repository;

import com.example.btg.model.FondoInversion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FondoInversionRepository extends MongoRepository<FondoInversion, String> {
    List<FondoInversion> findByActivoTrue();
    Optional<FondoInversion> findByIdAndActivoTrue(String id);
}
