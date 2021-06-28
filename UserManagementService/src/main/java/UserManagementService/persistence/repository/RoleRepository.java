package UserManagementService.persistence.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import UserManagementService.persistence.Entity.ERoleEntity;
import UserManagementService.persistence.Entity.RoleEntity;


public interface RoleRepository extends MongoRepository<RoleEntity, String> {
  Optional<RoleEntity> findByName(ERoleEntity name);
}
