# AWESOME PIZZA (a simple example)

## Prerequisites

- Java 25
- Docker desktop,Rancher Desktop installed or equivalent
- Postmam/Insomnia/Curl to simulate API calls
- Maven to build the project/run tests

## Disclaimer

For brevity all credentials are clearly visible in the code, so no need to set up any environment variable

## Database population

If you wanna add more pizza(s) just modify [this sql file](src/main/resources/db/migration/V2__add_pizzas.sql) and add entries according to the schema.
## Execute app

Run command "docker compose up" or via your IDE plugin,from the root of docker-compose.yml to start all docker images.
If you want to start only postgresql and rabbitmq as image and the application from your favorite IDE, just start it
with the env variable setup with the following data:

      - POSTGRES_HOST=localhost
      - POSTGRES_PORT=5432
      - POSTGRES_DB=awesome_pizza
      - POSTGRES_USER=s8
      - POSTGRES_PASS=s8
      - RABBITMQ_HOST=localhost
      - RABBITMQ_PORT=5672
      - RABBITMQ_USER=guest
      - RABBITMQ_PASS=guest

or launch spring application with local profile

### Endpoints

See [awesome-pizza-swagger.yaml](src/main/resources/swagger/awesome-pizza.yaml)

### Docker:

docker-compose volumes will be written under VOLUME_PATH see [env](.env)
You can change the value according to your needs.






