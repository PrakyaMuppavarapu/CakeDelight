# CakeDelight Event Contract

## 1. Overview

CakeDelight uses RabbitMQ for asynchronous communication between the Order Service and Notification Service.

When an order is successfully placed through checkout, the Order Service publishes an `OrderPlacedEvent`.

The Notification Service listens for this event and processes it as an order notification.

---

## 2. Event Flow

```text
Customer
    |
    v
API Gateway
    |
    v
Order Service
    |
    | OrderPlacedEvent
    v
RabbitMQ
    |
    v
Notification Service
    |
    v
Order Notification
```

This allows the Order Service to complete the order operation without directly calling the Notification Service.

---

## 3. RabbitMQ Configuration

The RabbitMQ configuration is defined in the Order Service.

| Component | Value |
|---|---|
| Exchange | `cakedelight.exchange` |
| Exchange Type | `TopicExchange` |
| Queue | `cakedelight.order.queue` |
| Routing Key | `order.placed` |

### Exchange

```text
cakedelight.exchange
```

The exchange is a topic exchange.

### Queue

```text
cakedelight.order.queue
```

The queue is durable.

### Routing Key

```text
order.placed
```

The routing key connects the order event to the notification queue.

---

## 4. Event Name

The event class is:

```text
OrderPlacedEvent
```

Package:

```text
com.cakedelight.order.event
```

The event is created after an order has been successfully saved.

---

## 5. Event Payload

The `OrderPlacedEvent` contains the following fields:

| Field | Type | Description |
|---|---|---|
| `orderId` | `Long` | ID of the placed order |
| `totalAmount` | `BigDecimal` | Total amount of the order |
| `customerName` | `String` | Name of the customer |
| `customerEmail` | `String` | Email address of the customer |

### Event Structure

```json
{
  "orderId": 1,
  "totalAmount": 1700.00,
  "customerName": "Customer",
  "customerEmail": "customer@example.com"
}
```

---

## 6. Event Producer

The Order Service acts as the event producer.

After checkout, the Order Service creates an `OrderPlacedEvent` containing:

- Order ID
- Total order amount
- Customer name
- Customer email

The event is published using RabbitMQ.

Conceptually:

```text
Order Service
      |
      | publish
      v
cakedelight.exchange
      |
      | routing key: order.placed
      v
cakedelight.order.queue
```

---

## 7. Event Consumer

The Notification Service acts as the event consumer.

It listens to:

```text
cakedelight.order.queue
```

When an event is received, the Notification Service processes the order notification.

The listener is implemented using:

```java
@RabbitListener(queues = "cakedelight.order.queue")
```

The notification is currently simulated through the application console.

Example output:

```text
=================================
NOTIFICATION SENT
New order received: ...
=================================
```

---

## 8. Message Conversion

RabbitMQ messages are configured to use JSON message conversion.

The Order Service uses:

```text
JacksonJsonMessageConverter
```

This allows Java event objects to be converted to JSON messages when they are sent through RabbitMQ.

---

## 9. Order-to-Notification Sequence

The complete event-driven sequence is:

### Step 1 — Customer Checkout

The customer checks out their basket through:

```text
POST /orders/checkout/{customerEmail}
```

### Step 2 — Order Creation

The Order Service:

1. Retrieves the basket.
2. Calculates the total.
3. Creates the order.
4. Creates the order items.

### Step 3 — Event Creation

The Order Service creates:

```text
OrderPlacedEvent
```

containing:

```text
orderId
totalAmount
customerName
customerEmail
```

### Step 4 — Event Publishing

The event is published to:

```text
cakedelight.exchange
```

using:

```text
order.placed
```

as the routing key.

### Step 5 — RabbitMQ Delivery

RabbitMQ routes the message to:

```text
cakedelight.order.queue
```

### Step 6 — Notification Processing

The Notification Service receives the event and generates the simulated notification.

---

## 10. Event-Driven Architecture

The communication can be represented as:

```text
                  Synchronous
              REST communication
                     |
                     v
             +---------------+
             | Order Service |
             +---------------+
                     |
                     |
               Asynchronous
                RabbitMQ
                     |
                     v
          +---------------------+
          | Notification Service|
          +---------------------+
```

The use of asynchronous messaging keeps the Notification Service decoupled from the Order Service.

---

## 11. RabbitMQ Components Summary

| Component | Configuration |
|---|---|
| Message Broker | RabbitMQ |
| Exchange | `cakedelight.exchange` |
| Exchange Type | Topic |
| Queue | `cakedelight.order.queue` |
| Routing Key | `order.placed` |
| Producer | Order Service |
| Consumer | Notification Service |
| Event | `OrderPlacedEvent` |
| Message Format | JSON |

---

## 12. Failure and Reliability Considerations

RabbitMQ provides asynchronous message delivery between the Order Service and Notification Service.

The queue is configured as durable:

```text
cakedelight.order.queue
```

This allows the queue definition to survive RabbitMQ restarts.

The application can also be inspected through Docker and Kubernetes logs to verify event processing and notification activity.

---

## 13. End-to-End Event Demonstration

A successful checkout can be demonstrated using:

```text
CakeDelight UI
      |
      v
Add cake to basket
      |
      v
Checkout
      |
      v
Order Service
      |
      v
OrderPlacedEvent
      |
      v
RabbitMQ
      |
      v
Notification Service
      |
      v
NOTIFICATION SENT
```

This demonstrates the event-driven communication required by the CakeDelight architecture.