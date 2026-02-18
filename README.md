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

## Run Locally (Docker)

From the project root:

```sh
docker compose up -d --build
```

After startup, open: [Swagger UI](http://localhost:8080/swagger-ui/index.html)

For authorization, use: `username: user1, password: password1` or `username: user2, password: password2`
