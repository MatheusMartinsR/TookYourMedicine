package com.matheus.tookYourMedicine.services;

import com.matheus.tookYourMedicine.dto.MedicineCreateDTO;
import com.matheus.tookYourMedicine.dto.MedicineDTO;
import com.matheus.tookYourMedicine.entity.MedicineEntity;
import com.matheus.tookYourMedicine.entity.UserEntity;
import com.matheus.tookYourMedicine.exception.NotFoundException;
import com.matheus.tookYourMedicine.message.MedicineMessage;
import com.matheus.tookYourMedicine.producer.MedicineProducer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class MedicineService {

  private final UserService userService;
  private final MedicineProducer medicineProducer;

  public MedicineService(UserService userService, MedicineProducer medicineProducer) {
    this.userService = userService;
    this.medicineProducer = medicineProducer;
  }

  private static final Map<UUID, MedicineEntity> medicines = new HashMap<>();

  public MedicineDTO createNewMedicine(MedicineCreateDTO dto) {
    UserEntity user = userService.findById(dto.getUserId());
    MedicineEntity medicine =
        new MedicineEntity(
            UUID.randomUUID(),
            dto.getMedicineName(),
            dto.getQuantityPerDay(),
            dto.getHourToTake(),
            false,
            user);

    medicines.put(medicine.getId(), medicine);

    medicineProducer.publish(
        MedicineMessage.builder()
            .medicineId(medicine.getId())
            .medicineName(medicine.getMedicineName())
            .hourToTake(medicine.getHourToTake())
            .userId(user.getId())
            .build());

    return new MedicineDTO(
        medicine.getId(),
        medicine.getMedicineName(),
        medicine.getQuantityPerDay(),
        medicine.getHourToTake(),
        user.getId(),
        medicine.getTake());
  }

  @Cacheable(value = "medicine", key = "#id")
  public MedicineDTO findMedicineById(UUID id) {
    MedicineEntity medicine = medicines.get(id);
    if (medicine == null) {
      throw new NotFoundException("Medicine not found with id: " + id);
    }
    return new MedicineDTO(
        medicine.getId(),
        medicine.getMedicineName(),
        medicine.getQuantityPerDay(),
        medicine.getHourToTake(),
        medicine.getUser() != null ? medicine.getUser().getId() : null,
        medicine.getTake());
  }

  @Cacheable(value = "medicine", key = "#name.toLowerCase()", unless = "#result == null")
  public MedicineDTO findMedicineByName(String name) {
    System.out.println("fetching data from Map, not from redis" + name);
    MedicineEntity medicineEntity =
        medicines.values().stream()
            .filter(m -> m.getMedicineName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);

    if (medicineEntity == null) {
      return null;
    }
    return new MedicineDTO(
        medicineEntity.getId(),
        medicineEntity.getMedicineName(),
        medicineEntity.getQuantityPerDay(),
        medicineEntity.getHourToTake(),
        medicineEntity.getUser() != null ? medicineEntity.getUser().getId() : null,
        medicineEntity.getTake());
  }

  @Cacheable(value = "medicineByUser", key = "#userId")
  public List<MedicineDTO> findMedicineByUserId(UUID userId) {
    return medicines.values().stream()
        .filter(m -> m.getUser() != null)
        .filter(m -> m.getUser().getId().equals(userId))
        .map(
            m ->
                new MedicineDTO(
                    m.getId(),
                    m.getMedicineName(),
                    m.getQuantityPerDay(),
                    m.getHourToTake(),
                    userId,
                    m.getTake()))
        .toList();
  }

  @Caching(
      evict = {
        @CacheEvict(value = "medicine", key = "#id"),
        @CacheEvict(value = "medicineByUser", allEntries = true)
      })
  public boolean markAsTaken(UUID id) {
    MedicineEntity medicine = medicines.get(id);
    if (medicine == null) {
      throw new NotFoundException("Medicine not found with id: " + id);
    }
    medicine.setTake(true);
    return true;
  }

  public boolean deleteMedicineById(UUID id) {
    return medicines.remove(id) != null;
  }
}
