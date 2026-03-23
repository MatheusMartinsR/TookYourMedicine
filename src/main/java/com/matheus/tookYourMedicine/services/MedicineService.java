package com.matheus.tookYourMedicine.services;

import com.matheus.tookYourMedicine.dto.MedicineCreateDTO;
import com.matheus.tookYourMedicine.dto.MedicineDTO;
import com.matheus.tookYourMedicine.entity.MedicineEntity;
import com.matheus.tookYourMedicine.entity.UserEntity;
import com.matheus.tookYourMedicine.exception.NotFoundException;
import com.matheus.tookYourMedicine.message.MedicineMessage;
import com.matheus.tookYourMedicine.producer.MedicineProducer;
import com.matheus.tookYourMedicine.repository.MedicineRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class MedicineService {

  private final UserService userService;
  private final MedicineProducer medicineProducer;
  private final MedicineRepository medicineRepository;

  public MedicineService(
      UserService userService,
      MedicineProducer medicineProducer,
      MedicineRepository medicineRepository) {
    this.userService = userService;
    this.medicineProducer = medicineProducer;
    this.medicineRepository = medicineRepository;
  }

  public MedicineDTO createNewMedicine(MedicineCreateDTO dto) {
    UserEntity user = userService.findById(dto.getUserId());

    MedicineEntity medicine =
        new MedicineEntity(
            null, dto.getMedicineName(), dto.getQuantityPerDay(), dto.getHourToTake(), false, user);

    MedicineEntity saved = medicineRepository.save(medicine);

    medicineProducer.publish(
        MedicineMessage.builder()
            .medicineId(saved.getId())
            .medicineName(saved.getMedicineName())
            .hourToTake(saved.getHourToTake())
            .userId(user.getId())
            .build());

    return toDTO(saved);
  }

  @Cacheable(value = "medicine", key = "#id", unless = "#result == null")
  public MedicineDTO findMedicineById(UUID id) {
    MedicineEntity medicine =
        medicineRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Medicine not found with id: " + id));
    return toDTO(medicine);
  }

  @Cacheable(value = "medicine", key = "#name.toLowerCase()", unless = "#result == null")
  public MedicineDTO findMedicineByName(String name) {
    MedicineEntity medicine =
        medicineRepository
            .findByMedicineName(name)
            .orElseThrow(() -> new NotFoundException("Medicine not found with name: " + name));
    return toDTO(medicine);
  }

  @Cacheable(value = "medicineByUser", key = "#userId")
  public List<MedicineDTO> findMedicineByUserId(UUID userId) {
    return medicineRepository.findByUserId(userId).stream().map(this::toDTO).toList();
  }

  @Caching(
      evict = {
        @CacheEvict(value = "medicine", key = "#id"),
        @CacheEvict(value = "medicineByUser", allEntries = true)
      })
  public boolean markAsTaken(UUID id) {
    MedicineEntity medicine =
        medicineRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Medicine not found with id: " + id));
    medicine.setTake(true);
    medicineRepository.save(medicine);
    return true;
  }

  @Caching(
      evict = {
        @CacheEvict(value = "medicine", key = "#id"),
        @CacheEvict(value = "medicineByUser", allEntries = true)
      })
  public boolean deleteMedicineById(UUID id) {
    if (!medicineRepository.existsById(id)) {
      throw new NotFoundException("Medicine not found with id: " + id);
    }
    medicineRepository.deleteById(id);
    return true;
  }

  private MedicineDTO toDTO(MedicineEntity medicine) {
    return new MedicineDTO(
        medicine.getId(),
        medicine.getMedicineName(),
        medicine.getQuantityPerDay(),
        medicine.getHourToTake(),
        medicine.getUser() != null ? medicine.getUser().getId() : null,
        medicine.getTake());
  }
}
