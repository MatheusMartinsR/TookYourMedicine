package com.matheus.tookYourMedicine.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.matheus.tookYourMedicine.dto.MedicineCreateDTO;
import com.matheus.tookYourMedicine.dto.MedicineDTO;
import com.matheus.tookYourMedicine.entity.UserEntity;
import com.matheus.tookYourMedicine.exception.NotFoundException;
import com.matheus.tookYourMedicine.producer.MedicineProducer;
import java.time.LocalTime;
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

  @InjectMocks private MedicineService medicineService;

  private UserEntity userEntity;
  private MedicineCreateDTO createDTO;

  @BeforeEach
  void setUp() {
    userEntity = new UserEntity(UUID.randomUUID(), "Matheus", "matheus@email.com", null);
    createDTO = new MedicineCreateDTO("Paracetamol", 2, LocalTime.of(8, 0), userEntity.getId());
  }

  @Test
  void shouldCreateMedicineWithTakeFalse() {
    when(userService.findById(createDTO.getUserId())).thenReturn(userEntity);

    MedicineDTO result = medicineService.createNewMedicine(createDTO);

    assertNotNull(result);
    assertEquals("Paracetamol", result.getMedicineName());
    assertFalse(result.getTake());
    verify(medicineProducer, times(1)).publish(any());
  }

  @Test
  void shouldMarkMedicineAsTaken() {
    when(userService.findById(createDTO.getUserId())).thenReturn(userEntity);
    MedicineDTO created = medicineService.createNewMedicine(createDTO);

    boolean result = medicineService.markAsTaken(created.getId());

    assertTrue(result);
    MedicineDTO updated = medicineService.findMedicineById(created.getId());
    assertTrue(updated.getTake());
  }

  @Test
  void shouldThrowNotFoundWhenMedicineNotFound() {
    UUID randomId = UUID.randomUUID();
    assertThrows(NotFoundException.class, () -> medicineService.findMedicineById(randomId));
  }

  @Test
  void shouldThrowNotFoundWhenMarkingNonExistentMedicine() {
    UUID randomId = UUID.randomUUID();
    assertThrows(NotFoundException.class, () -> medicineService.markAsTaken(randomId));
  }
}
