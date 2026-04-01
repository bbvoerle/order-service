package nl.bvo.orderservice.messaging;

import nl.bvo.orderservice.entity.Order;
import nl.bvo.orderservice.enums.OrderStatus;
import nl.bvo.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class OrderMessageListener {

    private static final Logger log = LoggerFactory.getLogger(OrderMessageListener.class);

    private final OrderRepository orderRepository;

    public OrderMessageListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @JmsListener(destination = "${app.jms.order-created-queue}")
    public void handleOrderCreated(Long orderId) throws InterruptedException {
        log.info("Received message for orderId: {}", orderId);

        Thread.sleep(2000);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if(order.getStatus() != OrderStatus.PROCESSED) {
            order.setStatus(OrderStatus.PROCESSED);
            orderRepository.save(order);
            log.info("Updated to PROCESSED status for orderId: {}", orderId);
        } else {
            log.info("Ignored processing since already processed, orderId: {}", orderId);
        }
    }
}
