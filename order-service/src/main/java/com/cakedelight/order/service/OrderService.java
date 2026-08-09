package com.cakedelight.order.service;

import com.cakedelight.order.entity.Order;
import com.cakedelight.order.repository.OrderRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.cakedelight.order.config.RabbitMQConfig;
import com.cakedelight.order.event.OrderPlacedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.cakedelight.order.basket.BasketItem;
import com.cakedelight.order.basket.BasketItemRepository;

import java.math.BigDecimal;
import com.cakedelight.order.repository.OrderItemRepository;
import com.cakedelight.order.entity.OrderItem;
import com.cakedelight.order.dto.CakeResponse;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BasketItemRepository basketItemRepository;
    private final RestClient restClient;
    private final RabbitTemplate rabbitTemplate;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        BasketItemRepository basketItemRepository,
                        RestClient restClient,
                        RabbitTemplate rabbitTemplate) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.basketItemRepository = basketItemRepository;
        this.restClient = restClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order saveOrder(Order order) {

        Boolean cakeExists = checkCakeExists(order.getCakeId());

        if (!cakeExists) {
            throw new RuntimeException(
                    "Cake with ID " + order.getCakeId() + " does not exist"
            );
        }

        order.setStatus("PLACED");

        Order savedOrder = orderRepository.save(order);

        OrderPlacedEvent event = new OrderPlacedEvent(
                savedOrder.getId(),
                savedOrder.getTotalAmount(),
                savedOrder.getCustomerName(),
                savedOrder.getCustomerEmail()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                event
        );

        return savedOrder;
    }

    public Order updateOrder(Long id, Order updatedOrder) {

        Order order = orderRepository.findById(id).orElse(null);

        if (order == null) {
            return null;
        }

        Boolean cakeExists = checkCakeExists(updatedOrder.getCakeId());

        if (!cakeExists) {
            throw new RuntimeException(
                    "Cake with ID " + updatedOrder.getCakeId() + " does not exist"
            );
        }

        order.setCakeId(updatedOrder.getCakeId());
        order.setQuantity(updatedOrder.getQuantity());
        order.setCustomerName(updatedOrder.getCustomerName());
        order.setCustomerEmail(updatedOrder.getCustomerEmail());
        order.setStatus(updatedOrder.getStatus());

        return orderRepository.save(order);
    }

    public BasketItem addToBasket(String customerEmail, BasketItem basketItem) {

        boolean cakeExists = checkCakeExists(basketItem.getCakeId());

        if (!cakeExists) {
            throw new RuntimeException(
                    "Cake with ID " + basketItem.getCakeId() + " does not exist"
            );
        }

        basketItem.setCustomerEmail(customerEmail);

        return basketItemRepository.save(basketItem);
    }

    public List<BasketItem> getBasket(String customerEmail) {
        return basketItemRepository.findByCustomerEmail(customerEmail);
    }

    public BasketItem updateBasketItem(
            String customerEmail,
            Long itemId,
            Integer quantity) {

        BasketItem item = basketItemRepository.findById(itemId).orElse(null);

        if (item == null || !item.getCustomerEmail().equals(customerEmail)) {
            return null;
        }

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        item.setQuantity(quantity);

        return basketItemRepository.save(item);
    }
    public void removeFromBasket(String customerEmail, Long itemId) {

        BasketItem item = basketItemRepository.findById(itemId).orElse(null);

        if (item == null || !item.getCustomerEmail().equals(customerEmail)) {
            throw new RuntimeException("Basket item not found");
        }

        basketItemRepository.delete(item);
    }

    public BigDecimal getBasketTotal(String customerEmail) {

        List<BasketItem> basketItems =
                basketItemRepository.findByCustomerEmail(customerEmail);

        BigDecimal total = BigDecimal.ZERO;

        for (BasketItem item : basketItems) {

            CakeResponse cake = restClient.get()
                    .uri(catalogServiceUrl + "/cakes/{id}", item.getCakeId())
                    .retrieve()
                    .body(CakeResponse.class);

            if (cake == null || cake.price() == null) {
                throw new RuntimeException(
                        "Unable to retrieve price for cake with ID "
                                + item.getCakeId()
                );
            }

            BigDecimal itemTotal = cake.price()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            total = total.add(itemTotal);
        }

        return total;
    }

    public Order checkout(String customerEmail) {

        List<BasketItem> basketItems =
                basketItemRepository.findByCustomerEmail(customerEmail);

        if (basketItems.isEmpty()) {
            throw new RuntimeException("Basket is empty");
        }

        BigDecimal total = getBasketTotal(customerEmail);

        BasketItem firstItem = basketItems.get(0);

        Order order = new Order();

        order.setCakeId(firstItem.getCakeId());
        order.setQuantity(firstItem.getQuantity());
        order.setCustomerEmail(customerEmail);
        order.setCustomerName("Customer");
        order.setTotalAmount(total);
        order.setStatus("PLACED");

        Order savedOrder = orderRepository.save(order);

        for (BasketItem basketItem : basketItems) {

            CakeResponse cake = restClient.get()
                    .uri(catalogServiceUrl + "/cakes/{id}", basketItem.getCakeId())
                    .retrieve()
                    .body(CakeResponse.class);

            if (cake == null || cake.price() == null) {
                throw new RuntimeException(
                        "Unable to retrieve price for cake with ID "
                                + basketItem.getCakeId()
                );
            }

            OrderItem orderItem = new OrderItem();

            orderItem.setOrderId(savedOrder.getId());
            orderItem.setCakeId(basketItem.getCakeId());
            orderItem.setQuantity(basketItem.getQuantity());
            orderItem.setPrice(cake.price());

            orderItemRepository.save(orderItem);
        }

        OrderPlacedEvent event = new OrderPlacedEvent(
                savedOrder.getId(),
                savedOrder.getTotalAmount(),
                savedOrder.getCustomerName(),
                savedOrder.getCustomerEmail()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                event
        );

        basketItemRepository.deleteAll(basketItems);

        return savedOrder;
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Value("${catalog.service.url:http://localhost:8080}")
    private String catalogServiceUrl;

    private Boolean checkCakeExists(Long cakeId) {

        try {
            restClient.get()
                    .uri(catalogServiceUrl + "/cakes/{id}", cakeId)
                    .retrieve()
                    .toBodilessEntity();

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}