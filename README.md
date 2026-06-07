# AWESOME PIZZA (a simple example)

## Prerequisites

- Java 25
- Docker desktop or Rancher Desktop installed
- Postmam/Insomnia/CUrl to simulate API calls
- Maven to build the project
- For brevity all credentials are clearly visible in the code, so no need to set up any environment variable

### Endpoint

#### New Order (POST)

url : http://localhost:8080/api/awesome-pizza/order

body :

{"user":"Francesco","pizzas":["Margherita"]}

- response 200: {"orderId": 651 }
- 500 if error occurs

#### Order status (GET)

url : http://localhost:8080/api/awesome-pizza/order/{orderId}

given an orderId,

ritorna una stringa che rappresenta lo stato dell'ordine evaso

- 404 order not found
- 500 if error occurs





