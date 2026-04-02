package nl.bvo.orderservice.dto;

import nl.bvo.orderservice.enums.OrderStatus;

import java.math.BigDecimal;

public record OrderDTO(
        Long id,
        String customerId,
        BigDecimal amount,
        OrderStatus status
) {}