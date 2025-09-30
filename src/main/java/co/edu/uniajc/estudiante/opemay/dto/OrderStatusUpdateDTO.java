package co.edu.uniajc.estudiante.opemay.dto;

import co.edu.uniajc.estudiante.opemay.model.OrderStatus;
import co.edu.uniajc.estudiante.opemay.model.PaymentStatus;

/**
 * DTO para actualizar el estado de un pedido
 */
public class OrderStatusUpdateDTO {
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;

    // Constructors
    public OrderStatusUpdateDTO() {}

    public OrderStatusUpdateDTO(OrderStatus orderStatus, PaymentStatus paymentStatus) {
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}