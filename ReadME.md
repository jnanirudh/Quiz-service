# Quiz Service
Microservice responsible for creating quiz sessions and serving them to students.

## Tech Stack
* Java 17 / Spring Boot
* Spring Cloud OpenFeign (Service-to-Service communication)
* Spring Data JPA (MySQL)

## Endpoints
### Quiz Management
* `POST /quiz/create?subject={s}&numQ={n}&title={t}` - Creates a quiz.
    * *Returns: Success message with the new Quiz ID.*
* `GET /quiz/get/{id}` - Fetches questions for a quiz (uses Question Service Feign Client).

## Internal Communication (Feign)
* Connects to `Question-Service` on `http://localhost:8081`.

## Database Configuration
* **Database Name:** `quiz_db`
* **Table:** `quiz` (stores Quiz Title and a List of Question IDs).