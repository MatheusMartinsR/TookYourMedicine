package com.matheus.tookYourMedicine.repository;

import com.matheus.tookYourMedicine.entity.UserEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  boolean existsByEmail(String email);
}
