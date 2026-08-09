package com.cakedelight.order.basket;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "basket_items")
@Data
public class BasketItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerEmail;

    private Long cakeId;

    private Integer quantity;
}