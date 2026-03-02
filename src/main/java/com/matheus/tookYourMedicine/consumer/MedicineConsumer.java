package com.matheus.tookYourMedicine.consumer;

import com.matheus.tookYourMedicine.config.RabbitMQConfig;
import com.matheus.tookYourMedicine.message.MedicineMessage;
import com.matheus.tookYourMedicine.producer.MedicineProducer;
import com.matheus.tookYourMedicine.services.MedicineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MedicineConsumer {

  private final MedicineService medicineService;
  private final MedicineProducer medicineProducer;

  @RabbitListener(queues = RabbitMQConfig.MEDICINE_QUEUE)
  public void consume(MedicineMessage message) {
    log.info("Message received for medicine: {}", message.getMedicineName());

    var medicine = medicineService.findMedicineById(message.getMedicineId());

    if (medicine == null) {
      log.warn("Medicine {} not found, discarding message", message.getMedicineId());
      return;
    }
    if (medicine.getTake()) {
      log.info("Medicine {} already taken, removing from queue", medicine.getMedicineName());
      return;
    }
    log.warn(
        " REMINDER: take {} (Scheduled time: {})",
        medicine.getMedicineName(),
        medicine.getHourToTake());
    medicineProducer.publish(message);
  }
}
