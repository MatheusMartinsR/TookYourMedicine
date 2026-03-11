package com.matheus.tookYourMedicine.controller;

import com.matheus.tookYourMedicine.dto.MedicineCreateDTO;
import com.matheus.tookYourMedicine.dto.MedicineDTO;
import com.matheus.tookYourMedicine.services.MedicineService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medicines")
public class MedicineController {

  private final MedicineService medicineService;

  public MedicineController(MedicineService medicineService) {
    this.medicineService = medicineService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<MedicineDTO> findById(@PathVariable UUID id) {
    return ResponseEntity.ok(medicineService.findMedicineById(id));
  }

  @GetMapping("/name")
  public ResponseEntity<MedicineDTO> findMedicineByName(@RequestParam String name) {
    MedicineDTO medicine = medicineService.findMedicineByName(name);

    if (medicine == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(medicine);
  }

  @GetMapping("/user/{userId}")
  public List<MedicineDTO> findMedicineByUserId(@PathVariable UUID userId) {
    return medicineService.findMedicineByUserId(userId);
  }

  @PostMapping("/create")
  public ResponseEntity<MedicineDTO> createNewMedicine(@RequestBody MedicineCreateDTO dto) {
    MedicineDTO created = medicineService.createNewMedicine(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("/{id}/taken")
  public ResponseEntity<Void> markAsTaken(@PathVariable UUID id) {
    medicineService.markAsTaken(id);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/delete")
  public ResponseEntity<String> deleteByMedicineId(@PathVariable UUID id) {
    boolean removed = medicineService.deleteMedicineById(id);

    if (!removed) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Medicine with this id " + id + " not found");
    }

    return ResponseEntity.ok("Medicine " + id + " deleted");
  }
}
