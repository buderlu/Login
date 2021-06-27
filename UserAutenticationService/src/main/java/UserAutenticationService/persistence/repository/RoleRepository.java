package UserAutenticationService.persistence.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import UserAutenticationService.persistence.Entity.ERoleEntity;
import UserAutenticationService.persistence.Entity.RoleEntity;


public interface RoleRepository extends MongoRepository<RoleEntity, String> {
  Optional<RoleEntity> findByName(ERoleEntity name);
}
