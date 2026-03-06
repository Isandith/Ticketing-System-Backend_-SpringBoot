# Ticketing System Backend (Spring Boot)

Backend service for a real-time ticketing simulation system.

This project provides:
- REST APIs to save and load ticket system configuration
- APIs to start and stop the ticketing simulation
- WebSocket (STOMP + SockJS) updates for live events
- MySQL persistence for configuration values

## Tech Stack

- Java 17
- Spring Boot 3.3.x
- Spring Web
- Spring Data JPA
- Spring WebSocket
- MySQL
- Maven

## Project Structure

```text
src/main/java/com/TicketingSystem/Ticketing/
  TicketingApplication.java
  Controllers/
    ConfigController.java
    Controller.java
  Services/
    ConfigService.java
  Entities/
    ConfigEntity.java
  Repository/
    ConfigRepo.java
  cli/
    TicketPool.java
    Vendor.java
    Customer.java
  config/
    WebSocketConfig.java
  util/
    Message.java
```

## Configuration

Application settings are defined in `src/main/resources/application.properties`.

Default values in this repository:
- `server.port=9090`
- `spring.datasource.url=jdbc:mysql://localhost:3306/Ticketing`
- `spring.datasource.username=root`
- `spring.datasource.password=...`

Update DB credentials before running in your environment.

## Database

The project uses one main entity:
- `ConfigEntity` (`config` table) with fields:
  - `maxTicketCapacity`
  - `totalTicketCapacity`
  - `ticketReleaseRate`
  - `ticketRetrievalRate`

`spring.jpa.hibernate.ddl-auto=update` will create/update schema automatically.

## REST API

Base URL (default): `http://localhost:9090`

### Set configuration

- Method: `POST`
- Path: `/config/setEntity`
- Body (JSON):

```json
{
  "maxTicketCapacity": 100,
  "totalTicketCapacity": 10,
  "ticketReleaseRate": 2000,
  "ticketRetrievalRate": 1500
}
```

### Load configuration

- Method: `GET`
- Path: `/config/load`

### Start simulation

- Method: `POST`
- Path: `/config/start`

### Stop simulation

- Method: `POST`
- Path: `/config/stop`

## WebSocket

STOMP endpoint:
- `/chat` (SockJS enabled)

Broker/topic:
- Subscribe to `/topic/messages`

Application destination prefix:
- `/app`

Example: clients can subscribe to `/topic/messages` to receive live vendor/customer/system events.

## How the Simulation Works

1. `startSystem()` initializes a shared `TicketPool`.
2. A `Vendor` thread releases tickets at the configured release rate.
3. Multiple `Customer` threads attempt to buy tickets at the configured retrieval rate.
4. `TicketPool` synchronizes access using `wait()`/`notifyAll()`.
5. Actions are broadcast over WebSocket to `/topic/messages`.

## Run Locally

### Prerequisites

- JDK 17+
- MySQL running locally
- Maven (or use Maven Wrapper)

### Steps

1. Create MySQL database:

```sql
CREATE DATABASE Ticketing;
```

2. Update `application.properties` with valid DB credentials.

3. Run application from project root:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

4. Service starts on `http://localhost:9090`.

## Testing

Run tests with:

```bash
./mvnw test
```

On Windows PowerShell:

```powershell
.\mvnw.cmd test
```

## Notes

- This repository also contains a nested duplicate project folder: `w2051598_Springboot/`.
- Main backend source used for normal development is at the root `src/` path.
