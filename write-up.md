# Project Write-up

## 1. What I asked the AI to do, and what I decided myself

I asked the AI to help build a Spring Boot URL shortener with PostgreSQL support, including short-code generation, redirect handling, custom alias support, and basic analytics. I also asked for help with containerization so the app could run consistently in Docker.

The core design decisions were made by me before implementation:
- use a database sequence for short-code IDs instead of random retries,
- make duplicate URLs return the same short code when no custom alias is provided,
- keep custom aliases in a separate uniqueness domain,
- keep the implementation simple and production-friendly without over-engineering the first version.

## 2. What I changed or corrected in the AI output

I reviewed the generated implementation and adjusted several details:
- I made sure the redirect endpoint returns the expected redirect status and location.
- I corrected the test setup so it does not depend on a real database or Docker container to run locally.
- I kept the project configuration simple and avoided hardcoding secrets in the repository.
- I added Docker support using environment variables for database connection settings.

## 3. Biggest trade-offs and alternatives considered

- Sequence-backed short codes vs. random-string generation
  - I chose a sequence + base62 encoding approach because it is deterministic, collision-free, and efficient.
  - A random-string approach would be simpler in some cases, but it can introduce retries and edge cases under load.

- Idempotent duplicate URLs vs. always creating a new row
  - I chose idempotency so the same URL shortens to the same code when no custom alias is used.
  - This avoids duplicate records and makes the app easier to reason about.

- Synchronous analytics updates vs. asynchronous tracking
  - I kept click tracking simple and synchronous for this version.
  - An asynchronous queue could reduce redirect latency later, but it adds complexity and eventual consistency.

## 4. What is still missing or what I would do next

If I had more time, I would improve the project by adding:
- rate limiting for `/shorten`,
- better validation and abuse prevention,
- delete or expire links,
- load testing for concurrent short-code generation,
- more complete integration tests against a real PostgreSQL instance.

## 5. Deployment and testing

The application has been deployed to Render at:
https://url-shortner-ryhb.onrender.com/

This deployment can be tested by sending requests to the published URL, for example by shortening a URL through the `/shorten` endpoint and then visiting the generated short link.

### Curl examples

#### Deployed app on Render
```bash
curl -X POST https://url-shortner-ryhb.onrender.com/shorten \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com"}'
```

```bash
curl --location 'https://url-shortner-ryhb.onrender.com/{code}'
````

```bash
curl -X GET https://url-shortner-ryhb.onrender.com/health
```

```bash
curl -X GET https://url-shortner-ryhb.onrender.com/api/analytics/<shortCode>
```

#### Local app on port 8080
```bash
curl -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com"}'
```

```bash
curl -X GET http://localhost:8080/health
```

```bash
curl -X GET http://localhost:8080/api/analytics/<shortCode>
```

## Run the app with Docker Compose

Use the following command from the project root. Replace the values with your own environment variables when you run it:


```bash
SPRING_DATASOURCE_URL=demourl \
SPRING_DATASOURCE_USERNAME=demouser \
SPRING_DATASOURCE_PASSWORD=demopassword \
docker compose up --build
```

This keeps the database credentials out of the repository and passes them in at runtime.
