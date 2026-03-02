package com.matheus.tookYourMedicine.dto;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDTO implements Serializable {

  private UUID id;
  private String medicineName;
  private int quantityPerDay;
  private LocalTime hourToTake;
  private UUID userId;
  private Boolean take;
}
