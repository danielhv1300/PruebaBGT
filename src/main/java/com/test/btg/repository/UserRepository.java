package com.test.btg.repository;

import com.test.btg.model.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
	Optional<User> findByEmail(String email);
}
