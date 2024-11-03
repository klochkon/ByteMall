# Customer Service 

## Overview

The `customer-service` microservice is responsible for managing customer data and orders in a MongoDB database, utilizing Kafka for asynchronous communication and Redis for caching. This service is part of an online store ecosystem, interacting with other microservices such as `notification-service` and `product-service` via HTTP requests.

## Features

- **Customer Management**: Store and manage user profiles, including personal details, cart items, and subscription preferences.
- **Order Management**: Handle order data linked to customers, including order contents and total cost.
- **Sales Tracking**: Record and manage sales information associated with customer accounts.
- **Caching**: Use Redis to cache frequently accessed customer and order data.
- **Kafka Integration**: Send notifications and updates related to customer registration and order events.

## Technologies

- **Database**: MongoDB for storing customer, order, and sales data.
- **Caching**: Redis for caching customer and order details.
- **Messaging**: Kafka for asynchronous event-driven communication.
- **Framework**: Spring Boot for the application framework.

## Data Model

### Collections

- **`customer`**: Stores general information about users, including personal details, contact information, and cart items.
- **`order`**: Stores customer orders, including the items in each order and the total cost.
- **`sale`**: Records sales associated with each customer, tracking discounts and promotional offers.

## Kafka Integration

### Producers
- **Topic**: `mail-topic`
    - **Payload**: `MailDTO` object containing email and name data.
    - **Purpose**: To notify `notification-service` of new user registrations or updates.

### Consumers
- **Topic**: `order-topic`
    - **Payload**: `OrderWithProductCartDTO`
    - **Purpose**: Listen for new orders and save them to the database.

## HTTP Clients

- **`NotificationClient`**: Used to send email notifications, particularly for storage updates when a product is restocked.
- **`ProductClient`**: Retrieves product details for the items in a customer's cart or order.

## Service Classes

### `CustomerService`

Handles customer-related functionality, including saving, updating, and retrieving customer information.

- **`saveCustomer(Customer customer)`**: Saves a new customer record, generates a sale record with a default discount, and sends a registration email through Kafka.
- **`updateCustomer(Customer customer)`**: Updates an existing customer's information in MongoDB.
- **`deleteCustomerById(String id)`**: Deletes a customer record by ID.
- **`findCustomerById(String id)`**: Retrieves a customer's profile and associated cart items, mapped with product details.
- **`findAllCustomer()`**: Fetches all customer records.
- **`findCustomerEmailAndNameById(String customerId)`**: Retrieves only email and name for a specified customer.
- **`customerIdentify(Map<String, String> productsWasOutMap)`**: Notifies customers via email when previously out-of-stock products are restocked.
- **`cleanCart(String id)`**: Empties the cart for a specified customer.

### `OrderService`

Handles order-related functionality, including saving, updating, and retrieving orders.

- **`saveOrder(OrderWithProductCartDTO orderDuplicateDTO)`**: Listens to Kafka for new order events, creates an `Order` object, and saves it to MongoDB.
- **`updateOrder(Order order)`**: Updates an existing order.
- **`deleteOrderById(String id)`**: Deletes an order record by ID.
- **`findOrderById(String id)`**: Fetches an order by ID, mapping product details to items in the order.

### `SaleService`

`SaleService` manages operations related to sales records, including creating new sales, updating sales data, and calculating discounts and totals for customer purchases.

- **`saveSale(Sale sale)`**: Saves a new sale record, associating it with a specific customer and applying any available discounts.
- **`updateSale(Sale sale)`**: Updates details of an existing sale, such as adjusting discount rates or updating the total sale amount.
- **`deleteSaleById(String id)`**: Deletes a sale record by its unique ID.
- **`findSaleById(String id)`**: Fetches a specific sale record, displaying the details of the sale along with any discounts applied.
- **`findAllSales()`**: Retrieves all sales records, listing all available details.
- **`calculateDiscount(Sale sale)`**: Calculates and applies a discount to the sale based on customer status or available promotions. This function is usually called when a new sale is saved or an existing one is updated.

## Endpoints

## `CustomerController`

### `POST /api/v1/customer`
**Description**: Creates a new customer record.  
**Request Body**: JSON payload with customer details (`name`, `email`, `phone number`, `address`).  
**Response**: Returns the saved customer record with assigned unique ID.

### `PUT /api/v1/customer`
**Description**: Updates an existing customer record.  
**Request Body**: JSON payload with updated customer details (e.g., new address or updated contact information).  
**Response**: Returns the updated customer record.

### `DELETE /api/v1/customer/{id}`
**Description**: Deletes a customer record by unique ID.  
**Parameters**:
- `id`: Customer ID.  
  **Response**: Confirmation of deletion.

### `GET /api/v1/customer/{id}`
**Description**: Retrieves details of a specific customer by ID.  
**Parameters**:
- `id`: Customer ID.  
  **Response**: Returns the customer record with full details.

### `GET /api/v1/customer`
**Description**: Fetches all customer records.  
**Response**: Returns a list of all customers.


## `OrderController`

### `POST /api/v1/order`
**Description**: Creates a new order.  
**Request Body**: JSON payload with order details (`customerId`, list of products, quantities, any specific instructions).  
**Response**: Returns the saved order with unique order ID and calculated total amount.

### `PUT /api/v1/order`
**Description**: Updates an existing order.  
**Request Body**: JSON payload with updated order details (e.g., adding items, changing quantities, modifying instructions).  
**Response**: Returns the updated order.

### `DELETE /api/v1/order/{id}`
**Description**: Deletes an order by unique ID.  
**Parameters**:
- `id`: Order ID.  
  **Response**: Confirmation of deletion.

### `GET /api/v1/order/{id}`
**Description**: Retrieves details of a specific order by ID.  
**Parameters**:
- `id`: Order ID.  
  **Response**: Returns the order with product details, quantities, and total amount.

### `GET /api/v1/order`
**Description**: Fetches all orders.  
**Response**: Returns a list of all orders with essential details (order ID, customer name, and total amount).


## `SaleController`

### `POST /api/v1/sale`
**Description**: Creates a new sale record.  
**Request Body**: JSON payload with sale details (`customerId`, total amount, any discount information).  
**Response**: Returns the saved sale record with calculated discount.

### `PUT /api/v1/sale`
**Description**: Updates an existing sale record.  
**Request Body**: JSON payload with updated sale details (e.g., new total amount, adjusted discount rate).  
**Response**: Returns the updated sale record.

### `DELETE /api/v1/sale/{id}`
**Description**: Deletes a sale record by unique ID.  
**Parameters**:
- `id`: Sale ID.  
  **Response**: Confirmation of deletion.

### `GET /api/v1/sale/{id}`
**Description**: Retrieves details of a specific sale by ID.  
**Parameters**:
- `id`: Sale ID.  
  **Response**: Returns the sale record, including discount details.
### `GET /api/v1/sale`
**Description**: Fetches all sale records.  
**Response**: Returns a list of all sale records.

## Running Purchase-Service on Local Machine

### Prerequisites

1. **Java JDK**: Ensure you have Java JDK 11 or higher installed.
    - To check the Java version, run the command:
      ```bash
      java -version
      ```

2. **Maven**: Make sure you have Maven installed for managing dependencies.
    - To check the Maven version, run the command:
      ```bash
      mvn -version
      ```

3. **Kafka**: You need to run Kafka and Zookeeper on your machine.
    - Download Kafka from the [official website](https://kafka.apache.org/downloads).
    - Extract the archive and start Zookeeper and Kafka by executing the following commands in the terminal:
      ```bash
      # Start Zookeeper
      bin\windows\zookeeper-server-start.bat config\zookeeper.properties
 
      # Start Kafka
      bin\windows\kafka-server-start.bat config\server.properties
      ```

4. **MongoDB**:
    - Ensure MongoDB is installed and running on your machine.
    - You can download it from the [official MongoDB website](https://www.mongodb.com/try/download/community).
    - Start the MongoDB server. Typically, you can do this by running:
      ```bash
      mongod
      ```

    - Update the `application.properties` (or `application.yml`) file in your project with the MongoDB connection settings:
      ```properties
      spring.data.mongodb.uri=mongodb_uri
      ```


### Steps to Run the Microservice

1. **Cloning the Repository**: If the microservice code is stored in a Git repository, first clone it:
   ```bash
   git clone https://github.com/klochkon/StoreProject
   cd customer-service
    ```


## Summary

The `customer-service` manages customer and order data, integrates with other services for notifications and stock updates, and uses Kafka for asynchronous messaging and Redis for caching. This documentation provides an overview of core functionalities, data models, Kafka topics, and REST API endpoints in `customer-service`.

***
## Contacts

- Phone: +380684290064
- Email: [nikitaklochko08@gmail.com](mailto:nikitaklochko08@gmail.com)
