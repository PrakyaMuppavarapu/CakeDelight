# CakeDelight API Documentation

## 1. Overview

CakeDelight is a cloud-native microservices application consisting of:

- Catalog Service
- Order Service
- Rating Service
- Notification Service
- API Gateway

The API Gateway acts as the main entry point for client requests.

### API Gateway URLs

| Environment | Base URL |
|---|---|
| Docker Compose | `http://localhost:8084` |
| Kubernetes | `http://localhost:30084` |

---

## 2. Catalog Service APIs

Base path:

`/cakes`

The Catalog Service manages cake product information.

### GET /cakes

Returns all cakes in the catalog.

**Example:**

```http
GET /cakes
```

**Response:** JSON array containing cake objects.

---

### GET /cakes/{id}

Returns a specific cake using its ID.

**Example:**

```http
GET /cakes/1
```

**Example Response:**

```json
{
  "id": 1,
  "name": "Chocolate Truffle",
  "description": "Rich chocolate cake",
  "category": "Chocolate",
  "price": 850.00,
  "available": true,
  "imageUrl": "chocolate.jpg"
}
```

---

### POST /cakes

Creates a new cake.

**Example Request:**

```json
{
  "name": "Chocolate Truffle",
  "description": "Rich chocolate cake",
  "category": "Chocolate",
  "price": 850.00,
  "available": true,
  "imageUrl": "chocolate.jpg"
}
```

---

### PUT /cakes/{id}

Updates an existing cake.

**Example:**

```http
PUT /cakes/1
```

**Example Request:**

```json
{
  "name": "Chocolate Truffle Deluxe",
  "description": "Premium chocolate cake",
  "category": "Chocolate",
  "price": 950.00,
  "available": true,
  "imageUrl": "chocolate.jpg"
}
```

---

### DELETE /cakes/{id}

Deletes a cake using its ID.

**Example:**

```http
DELETE /cakes/1
```

---

### GET /cakes/filter

Filters cakes by name, category, and price range.

**Query Parameters:**

| Parameter | Description |
|---|---|
| `name` | Filters by cake name |
| `category` | Filters by cake category |
| `minPrice` | Minimum cake price |
| `maxPrice` | Maximum cake price |

All parameters are optional and can be combined.

**Example:**

```http
GET /cakes/filter?category=Chocolate&minPrice=500&maxPrice=1500
```

---

## 3. Order Service APIs

Base path:

`/orders`

The Order Service manages orders, baskets, and checkout.

### GET /orders

Returns all orders.

**Example:**

```http
GET /orders
```

---

### GET /orders/{id}

Returns an order by ID.

**Example:**

```http
GET /orders/1
```

---

### POST /orders

Creates an order.

**Example Request:**

```json
{
  "cakeId": 1,
  "quantity": 2,
  "customerName": "Customer",
  "customerEmail": "customer@example.com",
  "totalAmount": 1700.00,
  "status": "PLACED"
}
```

---

### PUT /orders/{id}

Updates an existing order.

**Example:**

```http
PUT /orders/1
```

---

### DELETE /orders/{id}

Deletes an order.

**Example:**

```http
DELETE /orders/1
```

---

## 4. Basket APIs

The basket is managed by the Order Service.

### GET /orders/basket/{customerEmail}

Returns all items currently in a customer's basket.

**Example:**

```http
GET /orders/basket/customer@example.com
```

---

### GET /orders/basket/{customerEmail}/total

Calculates the total price of all items in the customer's basket.

**Example:**

```http
GET /orders/basket/customer@example.com/total
```

---

### POST /orders/basket/{customerEmail}

Adds an item to a customer's basket.

**Example Request:**

```json
{
  "cakeId": 1,
  "quantity": 2
}
```

---

### PUT /orders/basket/{customerEmail}/{itemId}

Updates the quantity of a basket item.

**Example Request:**

```json
{
  "quantity": 3
}
```

---

### DELETE /orders/basket/{customerEmail}/{itemId}

Removes an item from the customer's basket.

**Example:**

```http
DELETE /orders/basket/customer@example.com/1
```

---

## 5. Checkout API

### POST /orders/checkout/{customerEmail}

Completes checkout for the customer's basket.

**Example:**

```http
POST /orders/checkout/customer@example.com
```

**Checkout Process:**

1. Retrieves the customer's basket.
2. Checks that the basket is not empty.
3. Calculates the basket total.
4. Creates the order.
5. Creates order items.
6. Publishes an `OrderPlacedEvent`.
7. Sends the event through RabbitMQ.
8. Removes the items from the basket.

---

## 6. Rating Service APIs

Base path:

`/ratings`

The Rating Service manages cake ratings and reviews.

### GET /ratings

Returns all ratings.

**Example:**

```http
GET /ratings
```

---

### GET /ratings/{id}

Returns a rating by ID.

**Example:**

```http
GET /ratings/1
```

---

### POST /ratings

Creates a rating for a cake.

**Example Request:**

```json
{
  "cakeId": 1,
  "rating": 5,
  "review": "Amazing cake!",
  "customerName": "Customer"
}
```

The Rating Service verifies that the referenced cake exists before saving the rating.

---

### PUT /ratings/{id}

Updates an existing rating.

**Example Request:**

```json
{
  "cakeId": 1,
  "rating": 4,
  "review": "Very good cake!",
  "customerName": "Customer"
}
```

---

### GET /ratings/cake/{cakeId}

Returns all ratings associated with a particular cake.

**Example:**

```http
GET /ratings/cake/1
```

---

### GET /ratings/cake/{cakeId}/average

Calculates the average rating for a cake.

**Example:**

```http
GET /ratings/cake/1/average
```

**Example Response:**

```text
4.5
```

---

### DELETE /ratings/{id}

Deletes a rating.

**Example:**

```http
DELETE /ratings/1
```

---

## 7. Notification Service API

Base path:

`/notifications`

### POST /notifications

Sends a notification message.

The message is supplied as a request parameter.

**Example:**

```http
POST /notifications?message=Test%20notification
```

**Example Response:**

```text
Notification sent successfully!
```

The Notification Service also receives order events asynchronously from RabbitMQ.

---

## 8. RabbitMQ Event Communication

The Order Service publishes an order completion event after checkout.

### RabbitMQ Configuration

| Component | Value |
|---|---|
| Exchange | `cakedelight.exchange` |
| Exchange Type | Topic |
| Queue | `cakedelight.order.queue` |
| Routing Key | `order.placed` |

The Notification Service listens to:

`cakedelight.order.queue`

and processes the order event.

See [EVENT-CONTRACT.md](EVENT-CONTRACT.md) for the event structure.

---

## 9. Service Ports

| Service | Port |
|---|---:|
| Catalog Service | 8080 |
| Order Service | 8081 |
| Rating Service | 8082 |
| Notification Service | 8083 |
| API Gateway | 8084 |
| RabbitMQ | 5672 |
| RabbitMQ Management UI | 15672 |

### Kubernetes API Gateway

The Kubernetes NodePort exposes the API Gateway at:

`http://localhost:30084`

---

## 10. High-Level API Communication

```text
Client / Web UI
       |
       v
API Gateway
       |
       +--------------------+
       |                    |
       v                    v
Catalog Service        Order Service
       |                    |
       v                    |
Catalog Database            |
                            v
                         RabbitMQ
                            |
                            v
                    Notification Service

       Rating Service
             |
             v
       Rating Database
```

The services use REST APIs for synchronous communication and RabbitMQ for asynchronous order notification events.