package com.matheus.tookYourMedicine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
  public static final String MEDICINE_QUEUE = "medicine.queue";
  public static final String MEDICINE_WAITING = "medicine.waiting";
  public static final String MEDICINE_EXCHANGE = "medicine.exchange";
  public static final String MEDICINE_DLX = "medicine.dlx";

  @Bean
  public DirectExchange medicineExchange() {
    return new DirectExchange(MEDICINE_EXCHANGE);
  }

  @Bean
  public DirectExchange medicineDlx() {
    return new DirectExchange(MEDICINE_DLX);
  }

  @Bean
  public Queue medicineWaitingQueue() {
    return QueueBuilder.durable(MEDICINE_WAITING)
        .withArgument("x-dead-letter-exchange", MEDICINE_DLX)
        .withArgument("x-dead-letter-routing-key", MEDICINE_QUEUE)
        .withArgument("x-message-ttl", 3_600_000)
        .build();
  }

  @Bean
  public Queue medicineQueue() {
    return QueueBuilder.durable(MEDICINE_QUEUE).build();
  }

  @Bean
  public Binding waitingBinding() {
    return BindingBuilder.bind(medicineWaitingQueue())
        .to(medicineExchange())
        .with(MEDICINE_WAITING);
  }

  @Bean
  public Binding medicineBinding() {
    return BindingBuilder.bind(medicineQueue()).to(medicineDlx()).with(MEDICINE_QUEUE);
  }

  @Bean
  public MessageConverter messageConverter() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return new Jackson2JsonMessageConverter(mapper);
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(messageConverter());
    return template;
  }
}
