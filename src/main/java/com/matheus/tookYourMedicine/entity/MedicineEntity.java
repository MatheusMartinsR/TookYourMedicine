package com.matheus.tookYourMedicine.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medicines")
public class MedicineEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "medicine_name", nullable = false)
  private String medicineName;

  @Column(name = "quantity_per_day", nullable = false)
  private int quantityPerDay;

  @Column(name = "hour_to_take", nullable = false)
  private LocalTime hourToTake;

  @Column(nullable = false)
  private Boolean take;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;
}
