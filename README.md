# TEST PROJECT – THE NOTES APP

Spring Boot + MongoDB application that allows a user to store everyday notes.

### Functional Requirements

- User can create notes with:
    - **Title** (required)
    - **Created Date**
    - **Text** (required)
    - **Tags** (can be empty)
- Only allowed tags: **BUSINESS**, **PERSONAL**, **IMPORTANT**
- User can always **update** and **delete** notes
- App can obtain **stats per note**: number of **unique words** in note text, sorted descending  
  Example: text `"note is just a note"` → `{"note": 2, "is": 1, "just": 1, "a": 1}`
- App must **not allow** creating notes without **title** or **text**
- App must allow **listing** notes showing only **Title** and **Created Date**
    - Getting note text must be done via a separate page/request
- While listing notes, user can **filter by Tags**
- Notes are sorted **newest first**
- Listing supports **pagination**

---

## Tech Stack

- Java + Spring Boot
- MongoDB
- Docker + Docker Compose
- OpenAPI/Swagger UI

---

## Check remote [swagger](https://test-task.adammudrak.space/swagger-ui/index.html) if you don't want to run locally


## Run Locally (Docker)

From the project root:

```sh
docker compose up -d --build
```

After startup, open: [Swagger UI](http://localhost:8080/swagger-ui/index.html)

For authorization, use: `username: user1, password: password1` or `username: user2, password: password2`

Note that you need to deactivate [seeder](src/main/java/org/example/notes_app/config/DataSeederConfig.java) before moving this app to production.

## Things to consider

To complete this task, I thought of the architecture myself and wrote core business logic.
Used AI and google for MongoDB (never worked before), used AI for generating seeders and test data;

Also, I'd like to point out that in this task, I didn't use JWT authentication stored safely in cookies,
didn't cover all possible edge cases, and didn't implement even basic security measures (like not exposing .env)
because this seems to be out of scope for a test task. Idea was to showcase the ability to use Spring Boot 
and MongoDB, providing quick proof-of-concept drafts;

If you would like to see more advanced security, scheduled email senders, registration via confirmation, etc., kindly visit my [portfolio](https://landing.adammudrak.space/)
and check out my other projects.