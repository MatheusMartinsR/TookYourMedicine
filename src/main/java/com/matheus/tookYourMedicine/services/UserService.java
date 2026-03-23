package com.matheus.tookYourMedicine.services;

import com.matheus.tookYourMedicine.dto.UserCreateDTO;
import com.matheus.tookYourMedicine.dto.UserDTO;
import com.matheus.tookYourMedicine.entity.UserEntity;
import com.matheus.tookYourMedicine.exception.NotFoundException;
import com.matheus.tookYourMedicine.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserDTO createNewUser(UserCreateDTO dto) {
    if (userRepository.existsByEmail(dto.getEmail())) {
      throw new IllegalArgumentException("Email already in use: " + dto.getEmail());
    }
    UserEntity user = new UserEntity(null, dto.getName(), dto.getEmail(), dto.getPassword(), null);
    UserEntity saved = userRepository.save(user);
    return new UserDTO(saved.getId(), saved.getName(), saved.getEmail());
  }

  @Cacheable(value = "users", key = "#id", unless = "#result == null")
  public UserEntity findById(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
  }

  public List<UserDTO> findAllUsers() {
    return userRepository.findAll().stream()
        .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail()))
        .toList();
  }

  @CacheEvict(value = "users", key = "#id")
  public boolean deleteById(UUID id) {
    if (!userRepository.existsById(id)) {
      throw new NotFoundException("User not found with id: " + id);
    }
    userRepository.deleteById(id);
    return true;
  }
}
