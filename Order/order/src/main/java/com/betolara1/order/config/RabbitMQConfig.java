package com.betolara1.order.config;

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
    // Chaves que recebe do payments (retorna se esta ok ou deu erro ou cancelou)
    private static final String PAYMENT_OK_QUEUE = "payment.ok";
    private static final String PAYMENT_ERROR_QUEUE = "payment.error";
    private static final String PAYMENT_CANCEL_QUEUE = "payment.cancel";
    private static final String PAYMENT_PROCESSING_QUEUE = "payment.processing";
    private static final String PAYMENT_REFUND_QUEUE = "payment.refund";

    private static final String INVENTORY_RESERVED_QUEUE = "inventory.reserved";
    private static final String INVENTORY_OUT_OF_STOCK_QUEUE = "inventory.outOfStock";
    private static final String INVENTORY_AVAILABLE_QUEUE = "inventory.available";
    private static final String INVENTORY_ERROR_QUEUE = "inventory.error";

    // 2. A Fila (A caixa de correio do Payments)
    @Bean
    public Queue paymentOkQueue() {
        return new Queue(PAYMENT_OK_QUEUE);
    }

    @Bean
    public Queue paymentErrorQueue() {
        return new Queue(PAYMENT_ERROR_QUEUE);
    }

    @Bean
    public Queue paymentCancelQueue() {
        return new Queue(PAYMENT_CANCEL_QUEUE);
    }

    @Bean
    public Queue paymentProcessingQueue() {
        return new Queue(PAYMENT_PROCESSING_QUEUE);
    }

    @Bean
    public Queue paymentRefundQueue() {
        return new Queue(PAYMENT_REFUND_QUEUE);
    }

    @Bean
    public Queue inventoryReservedQueue() {
        return new Queue(INVENTORY_RESERVED_QUEUE);
    }

    @Bean
    public Queue inventoryOutOfStockQueue() {
        return new Queue(INVENTORY_OUT_OF_STOCK_QUEUE);
    }

    @Bean
    public Queue inventoryAvailableQueue() {
        return new Queue(INVENTORY_AVAILABLE_QUEUE);
    }

    @Bean
    public Queue inventoryErrorQueue() {
        return new Queue(INVENTORY_ERROR_QUEUE);
    }

    // 3. A Exchange (A agência dos Correios)
    @Bean
    public TopicExchange ecommerceExchange() {
        return new TopicExchange("ecommerce.exchange");
    }

    // 4. A Ligação (Avisando a Agência que a Etiqueta vai praquela Caixa)
    @Bean
    public Binding bindingOk(Queue paymentOkQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(paymentOkQueue).to(ecommerceExchange).with("payment.ok");
    }

    @Bean
    public Binding bindingError(Queue paymentErrorQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(paymentErrorQueue).to(ecommerceExchange).with("payment.error");
    }

    @Bean
    public Binding bindingCancel(Queue paymentCancelQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(paymentCancelQueue).to(ecommerceExchange).with("payment.cancel");
    }

    @Bean
    public Binding bindingProcessing(Queue paymentProcessingQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(paymentProcessingQueue).to(ecommerceExchange).with("payment.processing");
    }

    @Bean
    public Binding bindingRefund(Queue paymentRefundQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(paymentRefundQueue).to(ecommerceExchange).with("payment.refund");
    }

    @Bean
    public Binding bindingInventoryReserved(Queue inventoryReservedQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(inventoryReservedQueue).to(ecommerceExchange).with("inventory.reserved");
    }

    @Bean
    public Binding bindingInventoryOutOfStock(Queue inventoryOutOfStockQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(inventoryOutOfStockQueue).to(ecommerceExchange).with("inventory.outOfStock");
    }

    @Bean
    public Binding bindingInventoryAvailable(Queue inventoryAvailableQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(inventoryAvailableQueue).to(ecommerceExchange).with("inventory.available");
    }

    @Bean
    public Binding bindingInventoryError(Queue inventoryErrorQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(inventoryErrorQueue).to(ecommerceExchange).with("inventory.error");
    }

    // Converte Java Objects para JSON quando manda pra fila
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
