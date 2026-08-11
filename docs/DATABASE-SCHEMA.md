# CakeDelight Database Schema

## 1. Overview

CakeDelight follows a microservices architecture in which each service owns its own database.

The application uses PostgreSQL for persistent data storage.

Each database is isolated by service:

| Service | Database |
|---|---|
| Catalog Service | `catalog_db` |
| Order Service | `order_db` |
| Rating Service | `rating_db` |

The Notification Service does not currently maintain a separate database.

---

## 2. Catalog Database

Database:

`catalog_db`

Table:

`cakes`

### Cakes Table

| Column | Type | Description |
|---|---|---|
| `id` | BIGINT | Primary key |
| `name` | VARCHAR | Cake name |
| `description` | VARCHAR | Cake description |
| `category` | VARCHAR | Cake category |
| `price` | DECIMAL | Cake price |
| `available` | BOOLEAN | Whether the cake is available |
| `image_url` | VARCHAR | Reference to the cake image |

### Cake Entity

```text
Cake
├── id
├── name
├── description
├── category
├── price
├── available
└── imageUrl
```

---

## 3. Order Database

Database:

`order_db`

The Order Service manages orders, order items, and basket items.

### 3.1 Orders Table

Table:

`orders`

| Column | Type | Description |
|---|---|---|
| `id` | BIGINT | Primary key |
| `cake_id` | BIGINT | ID of the cake |
| `quantity` | INTEGER | Quantity ordered |
| `customer_name` | VARCHAR | Customer name |
| `customer_email` | VARCHAR | Customer email |
| `total_amount` | DECIMAL | Total order amount |
| `status` | VARCHAR | Current order status |

### Order Entity

```text
Order
├── id
├── cakeId
├── quantity
├── customerName
├── customerEmail
├── totalAmount
└── status
```

---

### 3.2 Order Items Table

Table:

`order_items`

| Column | Type | Description |
|---|---|---|
| `id` | BIGINT | Primary key |
| `order_id` | BIGINT | ID of the associated order |
| `cake_id` | BIGINT | ID of the cake |
| `quantity` | INTEGER | Quantity ordered |
| `price` | DECIMAL | Price of the cake at checkout |

Order items represent individual cake items belonging to an order.

---

### 3.3 Basket Items Table

Table:

`basket_items`

| Column | Type | Description |
|---|---|---|
| `id` | BIGINT | Primary key |
| `customer_email` | VARCHAR | Customer associated with the basket item |
| `cake_id` | BIGINT | ID of the selected cake |
| `quantity` | INTEGER | Quantity selected |

Basket items are managed by the Order Service.

---

## 4. Rating Database

Database:

`rating_db`

Table:

`ratings`

### Ratings Table

| Column | Type | Description |
|---|---|---|
| `id` | BIGINT | Primary key |
| `cake_id` | BIGINT | ID of the rated cake |
| `rating` | INTEGER | Rating value |
| `review` | VARCHAR | Customer review |
| `customer_name` | VARCHAR | Customer name |

### Rating Entity

```text
Rating
├── id
├── cakeId
├── rating
├── review
└── customerName
```

The Rating Service verifies that the referenced cake exists by communicating with the Catalog Service.

---

## 5. Database Ownership

Each microservice owns its own persistence mechanism.

```text
Catalog Service
      |
      v
  catalog_db
      |
     cakes


Order Service
      |
      v
   order_db
      |
      +-- orders
      +-- order_items
      +-- basket_items


Rating Service
      |
      v
   rating_db
      |
    ratings
```

Services do not directly access another service's database.

Instead, service-to-service information is obtained through REST APIs or asynchronous messaging.

For example:

```text
Rating Service
      |
      | REST API
      v
Catalog Service
      |
      v
Check whether cake exists
```

---

## 6. PostgreSQL Deployment

The PostgreSQL databases are deployed as separate components.

### Docker Compose

```text
catalog-postgres → catalog_db
order-postgres   → order_db
rating-postgres  → rating_db
```

Persistent Docker volumes are used:

```text
catalog-db-data
order-db-data
rating-db-data
```

These volumes allow database data to persist independently from application container restarts.

---

## 7. Kubernetes Database Components

The Kubernetes deployment contains separate PostgreSQL workloads for:

```text
catalog-postgres
order-postgres
rating-postgres
```

Each application service connects to its corresponding PostgreSQL service using Kubernetes service discovery.

For example:

```text
Catalog Service
      |
      v
catalog-postgres:5432
      |
      v
catalog_db
```

---

## 8. Data Flow

### Catalog

```text
Client
  |
  v
API Gateway
  |
  v
Catalog Service
  |
  v
catalog_db
```

### Order

```text
Client
  |
  v
API Gateway
  |
  v
Order Service
  |
  v
order_db
```

### Rating

```text
Client
  |
  v
API Gateway
  |
  v
Rating Service
  |
  +---- REST ----> Catalog Service
  |
  v
rating_db
```

### Notification

```text
Order Service
      |
      | OrderPlacedEvent
      v
   RabbitMQ
      |
      v
Notification Service
```

See [EVENT-CONTRACT.md](EVENT-CONTRACT.md) for details of the order completion event.