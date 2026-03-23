package com.matheus.tookYourMedicine.repository;

import com.matheus.tookYourMedicine.entity.MedicineEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineRepository extends JpaRepository<MedicineEntity, UUID> {
  Optional<MedicineEntity> findByMedicineName(String medicineName);

  List<MedicineEntity> findByUserId(UUID userId);
}
