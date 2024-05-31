# 🔌 Royaltis API: Backend for Real-Time Multiplayer Game Royaltis 🚀

## Overview 📝

The Royaltis API is a robust and customizable backend infrastructure designed specifically for real-time multiplayer games, particularly those inspired by the battle royale genre. This Java Spring Boot application, enhanced with Socket.IO, delivers a comprehensive solution for game data management, user authentication, lobby creation, and real-time game updates.

## Key Features ✨

- **Real-Time Gameplay:** ⚡ Utilizes Socket.IO for seamless, low-latency communication, ensuring a smooth and responsive multiplayer experience.
- **Flexible Game Data Management:** 💾
  - Stores game details (name, ID) in the database.
  - Supports defining custom borders, loot boxes (🎁), and their items within the database for high flexibility.
  - `MiddlePoint` (🎯) enables precise control over the center of the playable area, influencing game dynamics.
  - `DataRetriever` and `DataUploader` facilitate effortless retrieval and modification of game data, allowing for rapid iteration on game design.
- **User Authentication and Security:** 🔐
  - Implements secure token-based authentication using JWT (JSON Web Tokens).
  - Protects sensitive user information through encryption (`TokenManager`).
- **Dynamic Lobby System:** 🤝
  - Enables players to create and join lobbies for specific games, fostering social interaction and competition.
  - `LobbyManager` dynamically creates and manages lobby instances for efficient resource utilization.
  - Lobby codes provide a user-friendly way for players to join specific games together.
- **Scalable Architecture:** 📈
  - Built on Java Spring Boot, known for its scalability, the API is equipped to handle large numbers of concurrent users, ensuring a smooth experience even during peak times.
- **Customizable Game Logic:** ⚙️
  - The `Game` class handles the core game logic, primarily the border shrinking mechanism. This can be readily modified to accommodate diverse rules or game variations, allowing for unique gameplay experiences.

## High Customizability Through Environment Variables 🔧

The Royaltis API is engineered for adaptability. Key configurations are managed through environment variables, granting you the flexibility to tailor the system to your specific needs:

- **Database Configuration:** 💽
  - `royaltis_db_driver`, `royaltis_db_url`, `royaltis_db_user`, `royaltis_db_pass`: These environment variables control the database connection, simplifying the process of switching between different database providers or instances.

## Design and Implementation 🏗️

### Project Structure 📂

- `core.data`: Contains data models representing game elements (`GameData`, `LootBox`, `Borders`, `MiddlePoint`, etc.).
- `core.db`: Houses classes for database interactions (`DataUploader`, `DataRetriever`, `DBConnector`, `DbUtils`).
- `core.endpoints`: Includes REST API controllers handling actions like lobby creation, game joining, login, and game start.
- `core.game`: Contains the core game logic (`Game` class, `User` class, `Lobby` class).
- `core.managers`: Houses managers for lobbies (`LobbyManager`) and users (`UserManager`).
- `core.socket`: Handles Socket.IO configuration and event listeners for real-time communication.

### Core Components ⚙️

- **Database Interaction:** 🗃️
  - `DBConnector`: Establishes the connection to the MySQL database.
  - `DataRetriever`: Fetches game data from the database based on game ID.
  - `DataUploader`: Uploads new game data to the database.
  - `DbUtils`: Provides utility functions like checking if a game exists.
- **User Management:** 👤
  - `LoginCheck`: Authenticates users by validating their credentials against the database.
  - `TokenManager`: Generates and validates JWT tokens for secure user sessions.
  - `UserManager`: Manages connected users, tracks their locations within lobbies, and handles item interactions.
- **Lobby Management:** 🚪
  - `LobbyManager`: Creates, manages, and destroys game lobbies.
  - `Lobby`: Represents a single game session, storing player information and game state.
- **Game Logic:** 🧠
  - `Game`: Contains the core game logic, including the algorithm for moving borders towards the middle point during gameplay.
- **Socket.IO Integration:** 🌐
  - `SocketIOServer` and `Config`: Set up the Socket.IO server, define event handlers for actions like item deletion, location updates, user connections, and disconnections.

## Workflow 🔄

1. **Server Startup:** The Socket.IO server is initialized, and listens for client connections.
2. **Game Data:** Game data, including maps, loot box locations, and items, is either pre-loaded or uploaded via API calls.
3. **Lobby Creation:** Authenticated users can create lobbies for specific games.
4. **Joining Lobbies:** Users join lobbies using unique lobby codes.
5. **Game Start:** When everyone is ready, an authorized user starts the game. The server initiates the game loop and broadcasts real-time border updates to all connected clients in the lobby.
6. **Gameplay:** Players move within the game world, interact with items, and their actions are reflected in real-time for all participants. The server continuously maintains a synchronized game state for all clients.
7. **Game End:** The game concludes when the borders close, determining the winner based on the game's rules.

## Setup Instructions 🛠️

### Prerequisites

- Java JDK 8 or newer ☕
- MySQL database 🛢️
- Basic understanding of Spring Boot and Socket.IO 🧠

### Database Setup

1. Create a MySQL database.
2. Execute the provided SQL script to create the necessary tables (`Games`, `Borders`, `LootBoxes`, `LootBoxItems`, `MiddlePoints`, `Users`).

```sql
CREATE TABLE Games (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE Borders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    game_id INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    coords_latitude DOUBLE NOT NULL,
    coords_longitude DOUBLE NOT NULL,
    FOREIGN KEY (game_id) REFERENCES Games(id) ON DELETE CASCADE
);

CREATE TABLE LootBoxes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    game_id INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    coords_latitude DOUBLE NOT NULL,
    coords_longitude DOUBLE NOT NULL,
    FOREIGN KEY (game_id) REFERENCES Games(id) ON DELETE CASCADE
);

CREATE TABLE LootBoxItems (
    id INT AUTO_INCREMENT PRIMARY KEY,
    lootbox_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    FOREIGN KEY (lootbox_id) REFERENCES LootBoxes(id) ON DELETE CASCADE
);

CREATE TABLE MiddlePoints (
    id INT AUTO_INCREMENT PRIMARY KEY,
    game_id INT NOT NULL,
    type VARCHAR(50), 
    coords_latitude DOUBLE NOT NULL,
    coords_longitude DOUBLE NOT NULL,
    FOREIGN KEY (game_id) REFERENCES Games(id) ON DELETE CASCADE
);

CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL 
);
```
3. The database structure should look like this:

![Untitled(1)(1)](https://github.com/riveerxd/royaltisAPI/assets/132168494/93b56b0a-3bf8-4b51-a622-cbedacb5f7fa)


### Environment Variables 🔧

Set the following environment variables with your specific values:

- `royaltis_db_driver`, `royaltis_db_url`, `royaltis_db_user`, `royaltis_db_pass`: Configure your database connection 💽
- Optionally, you can customize `socketHost` and `socketPort` in `Config.java` to change the Socket.IO server settings ⚙️

### Building and Running 🛠️

#### Option 1: Customize and Build Yourself 🔨

1. Use Maven or your preferred build tool to compile the project. 🧱
2. Run the Spring Boot application using `mvn spring-boot:run` or a similar command. 🚀
3. The REST API server will start listening on port 8082, and the Socket.IO server will start listening on port 9090. 📡

#### Option 2: Download Pre-built JAR 📥

1. Download the pre-built JAR file from the [releases](https://github.com/riveerxd/royaltisAPI/releases/tag/Stable) page. 📦
2. Set the environment variables as described above. 🔧
3. Run the application from the command line using:
   ```bash
   java -jar royaltis-api.jar
   ```
4. The REST API server will start listening on port 8082, and the Socket.IO server will start listening on port 9090. 📡

## Conclusion 🎉

The Royaltis API provides a solid foundation for running real-time multiplayer games, offering flexibility, scalability, and a focus on customization. Its modular structure and use of proven technologies like Java Spring Boot and Socket.IO make it a maintainable and extensible solution for your game's backend needs.
