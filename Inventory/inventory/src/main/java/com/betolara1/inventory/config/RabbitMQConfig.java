package com.betolara1.inventory.config;

import org.springframework.amqp.core.Binding;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 1. A Chave de Roteamento (A "Etiqueta" do pacote)
    // Chaves que recebe do order (retorna se esta ok ou deu erro ou cancelou)
    private static final String INVENTORY_CREATED_QUEUE = "inventory.created";

    // 2. A Fila (A caixa de correio do order)
    @Bean
    public Queue inventoryReservedQueue() {
        return new Queue(INVENTORY_CREATED_QUEUE);
    }

    // 3. A Exchange (A agência dos Correios)
    @Bean
    public TopicExchange ecommerceExchange() {
        return new TopicExchange("ecommerce.exchange");
    }

    // 4. A Ligação (Avisando a Agência que a Etiqueta vai praquela Caixa)
    @Bean
    public Binding bindingInventoryCreated(Queue inventoryReservedQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(inventoryReservedQueue).to(ecommerceExchange).with("inventory.created");
    }

    // Converte Java Objects para JSON quando manda pra fila
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
