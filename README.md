# ByteMall

**ByteMall** is an online store designed for selling electronics and technology. 
On the website, users can buy and comment on products.

## Project Architecture

The project consists of **5 microservices** that interact with each other:

1. **Product**
    - Responsible for interacting with the database where product information and comments are stored.

2. **Storage**
    - Responsible for counting the quantity of products and checking stock availability during purchases.

3. **Notification**
    - Responsible for sending emails to users on various topics.

4. **Purchase**
    - A microservice that handles order placement.

5. **Customer**
    - Responsible for storing user information and their orders.

## Technologies Used

- **Java**
- **Spring**
- **Redis**
- **Kafka**
- **PostgreSQL** for the **Product** and **Storage** microservices
- **MongoDB** for the **Customer** microservice
- **Kubernetes** for orchestration
- **JUnit** and **Mockito** for unit testing
- **FeignClient** for sending HTTP requests between microservices
- **AWS S3 bucket** for saving products photos 

## Getting Started with Docker

### System Requirements
Before you start, ensure you have the following installed:
- **Docker** (latest version)
- **Docker Compose**

### Cloning the Repository
Clone the repository to your local machine using the following command:

```bash
git clone https://github.com/klochkon/StoreProject
```


***
## Contacts 

- Phone: +380684290064
- Email: [nikitaklochko08@gmail.com](mailto:nikitaklochko08@gmail.com)

