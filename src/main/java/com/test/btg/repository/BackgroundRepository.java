package com.test.btg.repository;

import com.test.btg.model.Background;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BackgroundRepository extends MongoRepository<Background, String> {
    Optional<Background> findByName(String name);
}
