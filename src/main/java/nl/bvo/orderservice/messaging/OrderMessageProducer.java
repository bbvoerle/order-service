package nl.bvo.orderservice.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderMessageProducer {

    private final JmsTemplate jmsTemplate;
    private final String orderCreatedQueue;

    public OrderMessageProducer(
            JmsTemplate jmsTemplate,
            @Value("${app.jms.order-created-queue}") String orderCreatedQueue
    ) {
        this.jmsTemplate = jmsTemplate;
        this.orderCreatedQueue = orderCreatedQueue;
    }

    public void sendOrderCreated(Long orderId) {
        jmsTemplate.convertAndSend(orderCreatedQueue, orderId);
    }
}
