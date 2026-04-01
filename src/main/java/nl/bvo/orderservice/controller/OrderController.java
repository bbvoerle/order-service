package nl.bvo.orderservice.controller;

import jakarta.validation.Valid;
import nl.bvo.orderservice.dto.OrderDTO;
import nl.bvo.orderservice.dto.OrderRequest;
import nl.bvo.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Void> createOrder(@Valid @RequestBody  OrderRequest orderRequest) {
        log.info("Received POST order request");
        orderService.createOrder(orderRequest);
        log.info("Finished POST order request");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<OrderDTO> findAllOrders() {
        log.info("Received GET all orders request");
        return orderService.findAllOrders();
    }
}
