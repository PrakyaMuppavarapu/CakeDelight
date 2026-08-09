package com.cakedelight.order.controller;

import com.cakedelight.order.entity.Order;
import com.cakedelight.order.service.OrderService;
import org.springframework.web.bind.annotation.*;
import com.cakedelight.order.basket.BasketItem;
import java.math.BigDecimal;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/basket/{customerEmail}")
    public List<BasketItem> getBasket(@PathVariable String customerEmail) {
        return orderService.getBasket(customerEmail);
    }
    @GetMapping("/basket/{customerEmail}/total")
    public BigDecimal getBasketTotal(@PathVariable String customerEmail) {
        return orderService.getBasketTotal(customerEmail);
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.saveOrder(order);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id,
                             @RequestBody Order order) {
        return orderService.updateOrder(id, order);
    }

    @PostMapping("/basket/{customerEmail}")
    public BasketItem addToBasket(
            @PathVariable String customerEmail,
            @RequestBody BasketItem basketItem) {

        return orderService.addToBasket(customerEmail, basketItem);
    }

    @PostMapping("/checkout/{customerEmail}")
    public Order checkout(@PathVariable String customerEmail) {
        return orderService.checkout(customerEmail);
    }

    @PutMapping("/basket/{customerEmail}/{itemId}")
    public BasketItem updateBasketItem(
            @PathVariable String customerEmail,
            @PathVariable Long itemId,
            @RequestBody BasketItem basketItem) {

        return orderService.updateBasketItem(
                customerEmail,
                itemId,
                basketItem.getQuantity()
        );
    }
    @DeleteMapping("/basket/{customerEmail}/{itemId}")
    public void removeFromBasket(
            @PathVariable String customerEmail,
            @PathVariable Long itemId) {

        orderService.removeFromBasket(customerEmail, itemId);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}