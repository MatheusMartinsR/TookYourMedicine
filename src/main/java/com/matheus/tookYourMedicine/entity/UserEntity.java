package com.matheus.tookYourMedicine.entity;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements Serializable {

  private UUID id;
  private String name;
  private String email;
  private String password;
}
