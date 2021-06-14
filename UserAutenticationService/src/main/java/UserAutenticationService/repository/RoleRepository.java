package UserAutenticationService.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import UserAutenticationService.models.ERole;
import UserAutenticationService.models.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
