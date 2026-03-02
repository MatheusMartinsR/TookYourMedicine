package com.matheus.tookYourMedicine.controller;

import com.matheus.tookYourMedicine.dto.MedicineCreateDTO;
import com.matheus.tookYourMedicine.dto.MedicineDTO;
import com.matheus.tookYourMedicine.entity.MedicineEntity;
import com.matheus.tookYourMedicine.services.MedicineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService){
        this.medicineService = medicineService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineDTO> findMedicineById(@PathVariable UUID id){
        MedicineDTO medicine = medicineService.findMedicineById(id);
        if (medicine == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(medicine);
    }

    @GetMapping("/name")
    public ResponseEntity<MedicineDTO> findMedicineByName(@RequestParam String name){
        MedicineDTO medicine = medicineService.findMedicineByName(name);

        if (medicine == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(medicine);
    }


    @GetMapping("/user/{userId}")
    public List<MedicineDTO> findMedicineByUserId(@PathVariable UUID userId){
        return medicineService.findMedicineByUserId(userId);
    }

    @PostMapping("/create")
    public ResponseEntity<MedicineDTO> createNewMedicine(
            @RequestBody MedicineCreateDTO dto
    ){
        MedicineDTO created = medicineService.createNewMedicine(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteByMedicineId(@PathVariable UUID id){
        boolean removed = medicineService.deleteMedicineById(id);

        if (!removed){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicine with this id " + id + " not found");
        }

        return ResponseEntity.ok("Medicine " + id + " deleted");
    }






}
