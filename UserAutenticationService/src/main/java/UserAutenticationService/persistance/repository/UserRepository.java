package UserAutenticationService.persistance.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import UserAutenticationService.persistance.Entity.UserEntity;

public interface UserRepository extends MongoRepository<UserEntity, String> {
  Optional<UserEntity> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}
