package UserAutenticationService.persistance.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import UserAutenticationService.persistance.Entity.ERoleEntity;
import UserAutenticationService.persistance.Entity.RoleEntity;


public interface RoleRepository extends MongoRepository<RoleEntity, String> {
  Optional<RoleEntity> findByName(ERoleEntity name);
}
