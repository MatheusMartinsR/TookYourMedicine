package com.matheus.tookYourMedicine.entity;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineEntity implements Serializable {

  private UUID id;
  private String medicineName;
  private int quantityPerDay;
  private LocalTime hourToTake;
  private Boolean take;
  private UserEntity user;
}
