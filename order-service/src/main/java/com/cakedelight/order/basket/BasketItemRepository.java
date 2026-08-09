package com.cakedelight.order.basket;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {

    List<BasketItem> findByCustomerEmail(String customerEmail);
}