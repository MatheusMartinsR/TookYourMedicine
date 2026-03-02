package com.matheus.tookYourMedicine.message;

import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineMessage {
  private UUID medicineId;
  private String medicineName;
  private LocalTime hourToTake;
  private UUID userId;
}
