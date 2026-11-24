# AWESOME PIZZA

A simple Spring Boot to manage a sort of Pizzeria with basic operations

## Prerequisites

- Java 25
- Docker or Docker/Rancher Desktop(Windows) installed
- Postmam/Insomnia/Curl to simulate RestFul API calls

## Disclaimer

For brevity all credentials are clearly visible in the code (DO NOT DO THIS IN REAL PROJECT)

## Database population

If you wanna add more pizza(s) just modify [this sql file](src/main/resources/db/migration/V5__add_pizzas.sql)and add entries according to the schema.
Or use the RESTFul endpoints.

## Execute app

Run `docker compose -f docker/docker-compose.yml up` from the repository root, or use your IDE's Docker Compose integration, to start all containers I

### Endpoints

See [awesome-pizza-swagger.yaml](src/main/resources/swagger/awesome-pizza.yaml)
or import the [Postman collection](postman/awesome-pizza.postman_collection.json).

### Docker:

Docker Compose bind-mount volumes will be written under `VOLUME_PATH`; see [docker/.env](docker/.env).
You can change the value according to your needs.

### Java AOT cache:

The Java 25 runtime can cache classes loaded at startup. Create a cache for the current app version and runtime:

1. Start a representative training run:

   ```sh
   docker compose -f docker/docker-compose.yml run --rm --service-ports -e AOT_MODE=record awesome-pizza
   ```

   Wait for the app to start, exercise the endpoints you expect to use, then stop it with Ctrl+C so the JVM can finish writing its training configuration.

2. Create the cache:

   ```sh
   docker compose -f docker/docker-compose.yml run --rm --no-deps -e AOT_MODE=create awesome-pizza
   ```

3. Start the app normally; the cache is loaded automatically when present:

   ```sh
   docker compose -f docker/docker-compose.yml up
   ```

The cache and training configuration are stored in the Compose `aot-cache` volume. Repeat the training and cache-creation steps after changing the application or its dependencies. The app still starts normally if no cache has been created.

