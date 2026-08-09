package com.cakedelight.order.entity;
import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cakeId;

    private Integer quantity;

    private String customerName;

    private String customerEmail;

    private BigDecimal totalAmount;

    private String status;
}
