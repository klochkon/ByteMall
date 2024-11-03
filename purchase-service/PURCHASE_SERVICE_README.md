# Purchase-Service 

## Overview

The Purchase-Service is responsible for managing the entire order process for products within the ByteMall platform. It interacts with other microservices through HTTP requests and Kafka to process purchases and manage inventory effectively.

## Kafka Integration

- **Kafka Producers**: The service produces messages to Kafka topics to trigger various operations:
    - **Order Topic**: Sends order information to the storage service for inventory management.
    - **Mail Topic**: Sends purchase confirmation emails to customers.
    - **Sale Topic**: Sends discount information to the sales topic if the order cost exceeds a certain threshold.

## Core Functionality

### Order Processing

The Purchase-Service provides the following key operations for managing the purchase process:

- **Process Purchase**: Handles the logic for processing a purchase, including checking inventory and notifying relevant services.
- **Send Purchase Email**: Sends a confirmation email to users after a successful purchase.
- **Clean Customer Cart**: Clears the customer's shopping cart after the order is processed.

### Inventory Management

- **Check Inventory**: Verifies if the ordered products are available in stock.
- **Find Out-of-Stock Products**: Identifies products that are out of stock and returns them to the caller.

## Endpoints

### POST /api/v1/purchase/operation

- **Description**: Processes a purchase for the given order details.
- **Request Body**: JSON payload containing the order details, including customer ID and cart.

### POST /api/v1/purchase/mail/send

- **Description**: Sends a purchase confirmation email to the customer.
- **Request Body**: JSON payload containing the order details.

## Purchase Logic

The purchase process includes logging for tracking the status of order processing. Each order is validated against the inventory, and appropriate actions are taken based on the inventory status. Emails are constructed and sent using the configured SMTP settings.

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



### Steps to Run the Microservice

1. **Cloning the Repository**: If the microservice code is stored in a Git repository, first clone it:
   ```bash
   git clone https://github.com/klochkon/StoreProject
   cd purchase-service
    ```


***
## Contacts

- Phone: +380684290064
- Email: [nikitaklochko08@gmail.com](mailto:nikitaklochko08@gmail.com)
