# Storage-Service

## Overview
The **Storage-Service** is responsible for managing product stock levels within the ByteMall platform. It is connected to a **PostgreSQL** database and uses **Redis** for caching. The service also communicates with other microservices through **Kafka**.

## Database
The service stores product stock information in a table with two columns:
- `productId`: Unique identifier for each product.
- `quantity`: Available quantity of the product in stock.

## Caching
The service implements caching for most of its operations using **Redis** to minimize database load and improve performance.

## Kafka Integration
- **Kafka Producer**: Sends messages to the `product-service` when stock changes occur.
- **Kafka Consumer**: Listens for order information from the `purchase-service` to update stock levels.

## Core Functionality

### 1. Product Stock Management
The service provides the following key operations for managing product stock:
- **Add Product**: Add a product to the storage by its ID and increase its quantity.
- **Update Product**: Update the stock quantity of an existing product.
- **Delete Product**: Remove a product by its ID.

### 2. Stock Verification
The service verifies stock levels periodically to check for products with low quantities (<= 10) and sends a Kafka message to notify admins.

### 3. Order Processing
The service processes order details received from the `purchase-service` via Kafka:
- Reduces the quantity of products based on the ordered amount.
- Notifies the `customer-service` if any products are out of stock.

## Endpoints

### `GET /api/v1/storage/check`
**Description**: Checks if a product is in stock.  
**Parameters**:
- `id`: The product ID.
- `requiredQuantity`: The quantity to check.

### `POST /api/v1/storage/save/{quantity}`
**Description**: Saves a new product with the specified quantity.  
**Parameters**:
- `quantity`: The quantity to be saved.
- `ProductDuplicateDTO`: Product data to be saved.

### `PUT /api/v1/storage/save/{quantity}`
**Description**: Updates an existing product's quantity.  
**Parameters**:
- `quantity`: The updated quantity.
- `ProductDuplicateDTO`: Product data to be updated.

### `GET /api/v1/storage/find/{id}`
**Description**: Retrieves product stock information by ID.  
**Parameters**:
- `id`: The product ID.

### `DELETE /api/v1/storage/delete/{id}`
**Description**: Deletes a product by its ID.  
**Parameters**:
- `id`: The product ID.

### `POST /api/v1/storage/add`
**Description**: Adds a product and increases its quantity.  
**Parameters**:
- `quantityAdded`: The amount to increase the stock.
- `ProductDuplicateDTO`: Product data.

### `GET /api/v1/storage/check/order`
**Description**: Verifies if all products in an order are in stock.  
**Parameters**:
- `cartDTO`: DTO with map of products and their quantities.

### `GET /api/v1/storage/find/order/out`
**Description**: Finds products that are out of stock in a given order.  
**Parameters**:
- `cartDTO`: DTO with map of products and their quantities.
- `customerId`: The customer ID.

### `GET /api/v1/storage/find/all`
**Description**: Retrieves all products with their stock quantities.

## Scheduling
A scheduled task runs daily at 7 AM to verify low stock products and send notifications through Kafka.

## Running Storage-Service on Local Machine

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

4. **PostgreSQL**:
    - Ensure PostgreSQL is installed and running on your machine.
    - You can download it from the [official PostgreSQL website](https://www.postgresql.org/download/).
    - Create a database for the Purchase-Service. You can do this using the `psql` command-line tool:
      ```sql
      CREATE DATABASE purchase_service_db;
      ```

    - Update the `application.properties` (or `application.yml`) file in your project with the PostgreSQL connection settings:
      ```properties
      spring.datasource.url = postgreSQL_url
      spring.datasource.username = your_username
      spring.datasource.password = your_password
      ```



### Steps to Run the Microservice

1. **Cloning the Repository**: If the microservice code is stored in a Git repository, first clone it:
   ```bash
   git clone https://github.com/klochkon/StoreProject
   cd storage-service
    ```


***
## Contacts

- Phone: +380684290064
- Email: [nikitaklochko08@gmail.com](mailto:nikitaklochko08@gmail.com)
