package nl.bvo.orderservice.dto;

import nl.bvo.orderservice.enums.OrderStatus;

import java.math.BigDecimal;

public class OrderDTO {

        private Long id;

        private String customerId;

        private BigDecimal amount;

        private OrderStatus status;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public OrderStatus getStatus() {
            return status;
        }

        public void setStatus(OrderStatus status) {
            this.status = status;
        }
}
