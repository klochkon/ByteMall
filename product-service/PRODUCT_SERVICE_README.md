# Product Service

## Description

The `product-service` is responsible for storing information about products, their comments, and interaction with cloud storage for storing product images. The service uses a PostgreSQL database and has three main tables:

- **Product**: contains information about products.
- **Comment**: stores comments on products.
- **ImageURL**: stores URLs of images of products stored in AWS S3.

## AWS S3 Configuration

The service integrates with AWS S3 for storing product photos. The configuration includes setting up access credentials and the region, as well as creating an AmazonS3 client for interacting with the cloud storage.

## Services

### ProductService

- **Description**: The `ProductService` class handles the business logic related to products. It performs CRUD (Create, Read, Update, Delete) operations for products and interacts with AWS S3 for storing images.

- **Key Methods**:
    - `getAllProductWithQuantity(List<StorageDuplicateDTO> storageList)`: Retrieves all products along with their quantities.
    - `createProduct(Product product, List<MultipartFile> photos)`: Creates a new product and stores images in AWS S3.
    - `deleteById(Long id)`: Deletes a product by its ID.
    - `findById(Long id)`: Retrieves a product by its ID.

### CommentService

- **Description**: The `CommentService` class manages the business logic related to product comments. It facilitates CRUD (Create, Read, Update, Delete) operations for comments associated with products and leverages caching for efficient data retrieval.

- **Key Methods**:
    - `findAllByProductId(Long productId)`: Retrieves all comments for a specific product identified by its ID.
    - `addComment(Comment comment)`: Adds a new comment for a product and updates the cache.
    - `updateComment(Comment comment)`: Updates an existing comment and refreshes the cache.
    - `deleteCommentById(Long id)`: Deletes a comment by its ID and removes it from the cache.
    - `findAllByAuthorNickname(String authorNickname)`: Retrieves all comments made by a specific author identified by their nickname.


## Database Entities

### Product

- **Fields**:
    - `id`: Unique identifier for the product.
    - `name`: Name of the product.
    - `description`: Description of the product.
    - `cost`: Price of the product.
    - `producer`: Producer of the product.
    - `category`: Category of the product.
    - `slug`: Unique slug for the product.
    - `feedBack`: Feedback rating for the product (1.0 to 5.0).
    - `comment`: List of comments associated with the product.
    - `imageUrl`: List of image URLs associated with the product.


### Comment

- **Fields**:
    - `id`: Unique identifier for the comment.
    - `authorNickname`: Nickname of the author of the comment.
    - `dateOfPublishing`: Date when the comment was published.
    - `comment`: Content of the comment.
    - `product`: Reference to the related product (with `productId` mapped internally).


### ImageURL

- **Fields**:
    - `id`: Unique identifier for the image URL.
    - `productId`: ID of the product the image relates to.
    - `url`: URL of the image stored in AWS S3.

## Endpoints

### `ProductController`

#### `POST /api/v1/products`
**Description**: Creates a new product record.  
**Request Body**: JSON payload with product details (`name`, `description`, `price`, `photos`).  
**Response**: Returns the saved product record with an assigned unique ID.

#### `PUT /api/v1/products`
**Description**: Updates an existing product record.  
**Request Body**: JSON payload with updated product details (e.g., new `description` or updated `price`).  
**Response**: Returns the updated product record.

#### `DELETE /api/v1/products/{id}`
**Description**: Deletes a product record by unique ID.  
**Parameters**:
- `id`: Product ID.  
  **Response**: Confirmation of deletion.

#### `GET /api/v1/products/{id}`
**Description**: Retrieves details of a specific product by ID.  
**Parameters**:
- `id`: Product ID.  
  **Response**: Returns the product record with full details.

#### `GET /api/v1/products`
**Description**: Fetches all product records.  
**Response**: Returns a list of all products.

### `CommentController`

#### `POST /api/v1/comments`
**Description**: Creates a new comment for a product.  
**Request Body**: JSON payload with comment details (`productId`, `text`).  
**Response**: Returns the saved comment record with an assigned unique ID.

#### `PUT /api/v1/comments`
**Description**: Updates an existing comment.  
**Request Body**: JSON payload with updated comment details (e.g., new `text`).  
**Response**: Returns the updated comment record.

#### `DELETE /api/v1/comments/{id}`
**Description**: Deletes a comment by unique ID.  
**Parameters**:
- `id`: Comment ID.  
  **Response**: Confirmation of deletion.

#### `GET /api/v1/comments/{id}`
**Description**: Retrieves details of a specific comment by ID.  
**Parameters**:
- `id`: Comment ID.  
  **Response**: Returns the comment record with full details.

#### `GET /api/v1/comments`
**Description**: Fetches all comments for a specific product.  
**Parameters**:
- `productId`: ID of the product.  
  **Response**: Returns a list of comments associated with the product.

### `ImageURLController`

#### `POST /api/v1/images`
**Description**: Creates a new image URL record for a product.  
**Request Body**: JSON payload with image details (`productId`, `url`).  
**Response**: Returns the saved image URL record with an assigned unique ID.

#### `DELETE /api/v1/images/{id}`
**Description**: Deletes an image URL record by unique ID.  
**Parameters**:
- `id`: Image URL ID.  
  **Response**: Confirmation of deletion.

#### `GET /api/v1/images/{id}`
**Description**: Retrieves details of a specific image URL by ID.  
**Parameters**:
- `id`: Image URL ID.  
  **Response**: Returns the image URL record with full details.

#### `GET /api/v1/images`
**Description**: Fetches all image URLs for a specific product.  
**Parameters**:
- `productId`: ID of the product.  
  **Response**: Returns a list of image URLs associated with the product.

## Dependencies

- **Spring Boot**: Core framework for building the service.
- **AWS SDK**: Library for interacting with AWS services.
- **PostgreSQL Driver**: Driver for connecting to PostgreSQL database.
- **Kafka**: Message broker for asynchronous communication between microservices.
- **Redis**: In-memory data structure store, used for caching and session management.

## Running Product-Service on Local Machine

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
    spring.datasource.url=database_url
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    ```



### Steps to Run the Microservice

1. **Cloning the Repository**: If the microservice code is stored in a Git repository, first clone it:
   ```bash
   git clone https://github.com/klochkon/StoreProject
   cd product-service
    ```

***
## Contacts

- Phone: +380684290064
- Email: [nikitaklochko08@gmail.com](mailto:nikitaklochko08@gmail.com)
