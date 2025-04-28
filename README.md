# Multiple Choice Questionnaires

A Spring Boot application for creating, managing, and taking multiple-choice questionnaires and surveys.

## Description

This application provides a platform for users to create and manage multiple-choice questionnaires. Users can create surveys with various questions, set time limits, assign points, and categorize them. Other users can take these surveys, and the system will automatically grade their answers and provide results.

## Key Features

- **User Authentication**: Secure login and registration with JWT token-based authentication
- **Survey Management**: Create, edit, and delete surveys
- **Question Management**: Add multiple-choice questions to surveys with various options
- **Categories**: Organize surveys by categories
- **Access Control**: Make surveys public or restrict access with passwords
- **Time Limits**: Set time limits for survey completion
- **Automatic Grading**: Automatic grading of answers and calculation of results
- **Result Tracking**: Track and view results of survey attempts

## Technologies Used

- **Backend**: Spring Boot, Spring Data JPA, Spring Security
- **Database**: MySQL
- **Authentication**: JWT (JSON Web Tokens)
- **Build Tool**: Maven
- **Containerization**: Docker, Docker Compose

## Project Structure

The project follows a clean architecture pattern with the following components:

- **Domain Layer**: Contains the core business logic and entities
- **Application Layer**: Contains use cases and service implementations
- **Infrastructure Layer**: Contains adapters for external services and repositories
- **API Layer**: Contains REST controllers for handling HTTP requests

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- MySQL

### Installation

1. Clone the repository
2. Configure the database connection in `application.properties`
3. Run `mvn clean install` to build the project
4. Run `mvn spring-boot:run` to start the application

### Docker Setup

You can also run the application using Docker:

1. Clone the repository
2. Copy the `.env.example` file to `.env` and configure it according to your needs
3. Run `docker-compose up -d` to start the application and MySQL database
4. Access the application at http://localhost:8080

To stop the containers:

```bash
docker-compose down
```

To rebuild the application after making changes:

```bash
docker-compose up -d --build
```

### Environment Configuration

The application uses environment variables for configuration. These can be set in the `.env` file when using Docker or in your system environment when running locally.

To configure the application:

1. Copy the `.env.example` file to `.env`
2. Modify the values in the `.env` file according to your needs

Available environment variables:

| Variable | Description | Default Value |
|----------|-------------|---------------|
| PORT | The port on which the application runs | 8080 |
| DB_HOST | Database host and port | mysql8:3306 |
| DB_DATABASE | Database name | multiple_choice_questionnaires |
| DB_USERNAME | Database username | root |
| DB_PASSWORD | Database password | root |
| SECRET_KEY | Secret key for JWT token generation | SSBhbSB0aGUgc3Rvcm0gdGhhdCBpcyBhcHByb2FjaGluZw== |
| TOKEN_EXPIRATION | JWT token expiration time in milliseconds | 3600000 |
| REFRESH_TOKEN_EXPIRATION | Refresh token expiration time in milliseconds | 86400000 |

## Usage

The application provides a RESTful API for interacting with the system. Users can:

- Register and login to get authentication tokens
- Create and manage surveys
- Take surveys and view results
- Manage their account details

## API Endpoints

The application exposes the following RESTful API endpoints:

### Authentication Endpoints

- `POST /api/v1/auth/register/creator`: Registers a new user with the SURVEY_CREATOR role
- `POST /api/v1/auth/register/respondent`: Registers a new user with the SURVEY_RESPONDENT role
- `POST /api/v1/auth/login`: Authenticates a user and returns JWT tokens
- `POST /api/v1/auth/refresh-token`: Refreshes an expired JWT token

### Account Endpoints

- `GET /api/v1/accounts`: Gets the profile of the current user
- `PUT /api/v1/accounts`: Updates the account information of the current user

### Category Endpoints

- `POST /api/v1/categories`: Creates a new category
- `GET /api/v1/categories`: Gets all categories (public endpoint)
- `GET /api/v1/categories/admin`: Gets all categories (admin endpoint with more filtering options)
- `GET /api/v1/categories/creator`: Gets all categories (creator endpoint)
- `PUT /api/v1/categories/{categoryId}`: Updates a category
- `DELETE /api/v1/categories/{categoryId}`: Deletes a category

### Survey Endpoints

- `POST /api/v1/surveys`: Creates a new survey
- `GET /api/v1/surveys`: Gets all surveys (for respondents)
- `GET /api/v1/surveys/creator`: Gets all surveys (for creators)
- `GET /api/v1/surveys/admin`: Gets all surveys (for admins)
- `GET /api/v1/surveys/{surveyId}`: Gets a survey by ID
- `GET /api/v1/surveys/{surveyId}/questions`: Gets all questions for a survey
- `PUT /api/v1/surveys/{surveyId}`: Updates a survey
- `DELETE /api/v1/surveys/{surveyId}`: Deletes a survey
- `GET /api/v1/surveys/{surveyId}/accounts`: Gets all accounts that have taken a survey (for creators)
- `GET /api/v1/surveys/{surveyId}/accounts/admin`: Gets all accounts that have taken a survey (for admins)
- `POST /api/v1/surveys/submit`: Submits a completed survey

### Question Endpoints

- `POST /api/v1/questions`: Creates a new question
- `GET /api/v1/questions/{questionId}`: Gets a question by ID
- `PUT /api/v1/questions/{questionId}`: Updates a question
- `DELETE /api/v1/questions/{questionId}`: Deletes a question

### Answer Endpoints

- `GET /api/v1/answers/{resultId}`: Lists answers by result ID

### Result Endpoints

- `GET /api/v1/results/{resultId}`: Gets a result by ID
- `GET /api/v1/results/survey/{surveyId}`: Gets results by survey ID (for survey creators)
- `GET /api/v1/results/account`: Gets all results for the current user
- `GET /api/v1/results/survey/{surveyId}/account/{accountId}`: Gets results for a specific survey and account

## API Documentation

The application provides interactive API documentation using Swagger UI. You can access the Swagger UI interface at:

```
http://localhost:8080/swagger-ui/index.html#/
```

### Using Swagger UI

Swagger UI provides a user-friendly interface to:

1. Explore all available API endpoints
2. View request parameters and response models
3. Test API endpoints directly from the browser
4. Authenticate using JWT tokens for protected endpoints

To use protected endpoints in Swagger UI:
1. First, use the authentication endpoints to obtain a JWT token
2. Click the "Authorize" button at the top of the page
3. Enter your JWT token in the format: `Bearer your_token_here`
4. Click "Authorize" to apply the token to all subsequent requests
