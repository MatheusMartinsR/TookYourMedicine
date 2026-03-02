package com.matheus.tookYourMedicine.producer;

import com.matheus.tookYourMedicine.config.RabbitMQConfig;
import com.matheus.tookYourMedicine.message.MedicineMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MedicineProducer {

  private final RabbitTemplate rabbitTemplate;

  public void publish(MedicineMessage message) {
    log.info("publishing message for medicine queue: {}", message.getMedicineName());

    rabbitTemplate.convertAndSend(
        RabbitMQConfig.MEDICINE_EXCHANGE, RabbitMQConfig.MEDICINE_WAITING, message);

    log.info("message published with sucess! id: {} ", message.getMedicineId());
  }
}
