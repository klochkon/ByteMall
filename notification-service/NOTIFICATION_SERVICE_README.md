# Notification-Service

## Overview
The **Notification-Service** is responsible for sending email notifications to users and administrators based on various events occurring within the ByteMall platform. The service integrates with other microservices through **Kafka** to receive triggering events for email notifications.

## Kafka Integration
- **Kafka Consumer**: Listens for messages from different topics to trigger email notifications:
    - **Purchase Confirmation**: Listens for purchase-related messages to send order confirmation emails.
    - **Registration Confirmation**: Listens for user registration messages to send welcome emails.
    - **Product Verification**: Listens for verification messages specifically for storage administrators.

## Core Functionality

### Email Notification Management
The service uses **Thymeleaf** for rendering HTML email templates, allowing for dynamic content to be included in emails based on the context.
Provides the following key operations for managing email notifications:
- **Send Purchase Email**: Sends a confirmation email to users after a successful purchase.
- **Send Registration Email**: Sends a welcome email to users after they register.
- **Send Product Verification Email**: Sends verification emails to administrators regarding product verification requests.
- **Send Update Storage Email**: Sends notifications to administrators about updates in product storage.

## Endpoints

### `POST /api/v1/notification/send/purchase/{to}`
**Description**: Sends a purchase confirmation email to the specified recipient.  
**Parameters**:
- `to`: The email address of the recipient.
- **Request Body**: JSON payload containing necessary data for the email (e.g., order details).

### `POST /api/v1/notification/send/registration/{to}`
**Description**: Sends a registration confirmation email to the specified recipient.  
**Parameters**:
- `to`: The email address of the recipient.
- **Request Body**: JSON payload containing user registration data.

### `POST /api/v1/notification/send/verification`
**Description**: Sends a product verification email to the storage administrator.  
**Request Body**: JSON payload containing data related to the product verification.

### `POST /api/v1/notification/send/storage/update`
**Description**: Sends an email notification regarding updates in storage to relevant administrators.  
**Request Body**: JSON payload containing storage update information.

## Email Sending Logic
The email sending process includes logging for tracking the status of email preparation and delivery. Each email is constructed using a template that is populated with relevant data before being sent through the `JavaMailSender`.

# Running Notification-Service on Local Machine

To run **Notification-Service** on your local machine, follow these steps:

## Prerequisites

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

4. **Email Settings**: Ensure you have SMTP settings for sending emails. Configure the environment variables in the `application.properties` (or `application.yml`) file in your project.

## Steps to Run the Microservice

1. **Cloning the Repository**:
   If the microservice code is stored in a Git repository, first clone it:
   ```bash
   git clone https://github.com/klochkon/StoreProject
   cd notification-service
   ```

***
## Contacts
- Phone: +380684290064
- Email: [nikitaklochko08@gmail.com](mailto:nikitaklochko08@gmail.com)
