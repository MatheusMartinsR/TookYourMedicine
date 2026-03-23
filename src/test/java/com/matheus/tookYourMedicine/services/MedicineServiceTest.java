package com.matheus.tookYourMedicine.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.matheus.tookYourMedicine.dto.MedicineCreateDTO;
import com.matheus.tookYourMedicine.dto.MedicineDTO;
import com.matheus.tookYourMedicine.entity.MedicineEntity;
import com.matheus.tookYourMedicine.entity.UserEntity;
import com.matheus.tookYourMedicine.exception.NotFoundException;
import com.matheus.tookYourMedicine.producer.MedicineProducer;
import com.matheus.tookYourMedicine.repository.MedicineRepository;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MedicineServiceTest {

  @Mock private UserService userService;
  @Mock private MedicineProducer medicineProducer;
  @Mock private MedicineRepository medicineRepository;

  @InjectMocks private MedicineService medicineService;

  private UserEntity userEntity;
  private MedicineCreateDTO createDTO;
  private MedicineEntity medicineEntity;

  @BeforeEach
  void setUp() {
    userEntity = new UserEntity(UUID.randomUUID(), "Matheus", "matheus@email.com", "123456", null);
    createDTO = new MedicineCreateDTO("Paracetamol", 2, LocalTime.of(8, 0), userEntity.getId());
    medicineEntity =
        new MedicineEntity(
            UUID.randomUUID(), "Paracetamol", 2, LocalTime.of(8, 0), false, userEntity);
  }

  @Test
  void shouldCreateMedicineWithTakeFalse() {
    when(userService.findById(createDTO.getUserId())).thenReturn(userEntity);
    when(medicineRepository.save(any())).thenReturn(medicineEntity); // ← mock do save

    MedicineDTO result = medicineService.createNewMedicine(createDTO);

    assertNotNull(result);
    assertEquals("Paracetamol", result.getMedicineName());
    assertFalse(result.getTake());
    verify(medicineProducer, times(1)).publish(any());
  }

  @Test
  void shouldMarkMedicineAsTaken() {
    medicineEntity.setTake(false);
    when(medicineRepository.findById(medicineEntity.getId()))
        .thenReturn(Optional.of(medicineEntity));
    when(medicineRepository.save(any())).thenReturn(medicineEntity);

    boolean result = medicineService.markAsTaken(medicineEntity.getId());

    assertTrue(result);
    assertTrue(medicineEntity.getTake()); // verifica direto na entidade
  }

  @Test
  void shouldThrowNotFoundWhenMedicineNotFound() {
    UUID randomId = UUID.randomUUID();
    when(medicineRepository.findById(randomId))
        .thenReturn(Optional.empty()); // ← mock retorna vazio

    assertThrows(NotFoundException.class, () -> medicineService.findMedicineById(randomId));
  }

  @Test
  void shouldThrowNotFoundWhenMarkingNonExistentMedicine() {
    UUID randomId = UUID.randomUUID();
    when(medicineRepository.findById(randomId))
        .thenReturn(Optional.empty()); // ← mock retorna vazio

    assertThrows(NotFoundException.class, () -> medicineService.markAsTaken(randomId));
  }
}
