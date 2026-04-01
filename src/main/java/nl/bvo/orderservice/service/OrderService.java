package nl.bvo.orderservice.service;

import nl.bvo.orderservice.dto.OrderDTO;
import nl.bvo.orderservice.dto.OrderRequest;
import nl.bvo.orderservice.entity.MessageOut;
import nl.bvo.orderservice.entity.Order;
import nl.bvo.orderservice.enums.MessageStatus;
import nl.bvo.orderservice.enums.OrderStatus;
import nl.bvo.orderservice.repository.MessageOutRepository;
import nl.bvo.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final MessageOutRepository messageOutRepository;

    public OrderService(OrderRepository orderRepository, MessageOutRepository messageOutRepository) {
        this.orderRepository = orderRepository;
        this.messageOutRepository = messageOutRepository;
    }

    @Transactional
    public void createOrder(OrderRequest orderRequest) {
        log.info("Creating new order.");

        Order order = createOrderEntity(orderRequest);
        order = orderRepository.save(order);
        log.info("Order created, orderId: {}", order.getId());

        MessageOut messageOut = createMessageOutEntity(order.getId());
        messageOut = messageOutRepository.save(messageOut);
        log.info("Order prepared for queue, orderId: {}, messageOutId: {}", order.getId(), messageOut.getId());
    }

    private MessageOut createMessageOutEntity(Long id) {
        MessageOut messageOut = new MessageOut();
        messageOut.setStatus(MessageStatus.NEW);
        messageOut.setOrderId(id);
        return messageOut;
    }

    private Order createOrderEntity(OrderRequest orderRequest) {
        Order order = new Order();
        order.setCustomerId(orderRequest.getCustomerId());
        order.setAmount(orderRequest.getAmount());
        order.setStatus(OrderStatus.CREATED);
        return order;
    }

    public List<OrderDTO> findAllOrders() {
        return StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .map(this::mapToOrderDTO)
                .collect(Collectors.toList());
    }

    private OrderDTO mapToOrderDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setCustomerId(order.getCustomerId());
        orderDTO.setAmount(order.getAmount());
        orderDTO.setStatus(order.getStatus());
        return orderDTO;
    }
}