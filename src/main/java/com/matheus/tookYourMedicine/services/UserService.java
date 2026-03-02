package com.matheus.tookYourMedicine.services;


import com.matheus.tookYourMedicine.dto.UserDTO;
import com.matheus.tookYourMedicine.entity.UserEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
public class UserService {

    private static final Map<UUID, UserEntity> users = new HashMap<>();

    public UserDTO createNewUser(UserDTO dto) {
        UserEntity user = new UserEntity(
                UUID.randomUUID(),
                dto.getName(),
                dto.getEmail(),
                null
        );

        users.put(user.getId(), user);

        return new UserDTO(user.getId(),user.getName(), user.getEmail());
    }

    @Cacheable(value = "users", key="#id", unless = "#result == null")
    public UserEntity findById(UUID id){
        return users.get(id);
    }

    public List<UserDTO> findAllUsers() {
        return users.values().stream().map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail())).toList();
    }

    public boolean deleteById(UUID id){
        return users.remove(id) != null;
    }



}
