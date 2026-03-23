package com.matheus.tookYourMedicine.controller;

import com.matheus.tookYourMedicine.dto.UserCreateDTO;
import com.matheus.tookYourMedicine.dto.UserDTO;
import com.matheus.tookYourMedicine.entity.UserEntity;
import com.matheus.tookYourMedicine.services.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/create")
  public ResponseEntity<UserDTO> createNewUser(@RequestBody UserCreateDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createNewUser(dto));
  }

  @GetMapping("/all")
  public List<UserDTO> getAllUSers() {
    return userService.findAllUsers();
  }

  @GetMapping("/{id}")
  public UserEntity getUserById(@PathVariable UUID id) {
    return userService.findById(id);
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<String> deleteUserById(@PathVariable UUID id) {
    boolean removed = userService.deleteById(id);

    if (!removed) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("User with this id " + id + " not found ");
    }

    return ResponseEntity.ok("User " + id + " deleted!");
  }
}
