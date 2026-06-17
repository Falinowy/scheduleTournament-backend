# Schedule Tournament API 🏆

Backend for the Schedule Tournament application. It provides REST APIs for managing tournaments, games, teams, standings, and locations. Built with **Java 17**, **Spring Boot 3**, and uses **Firebase Realtime Database** as its data store.

## 🚀 Technologies

* **Java 17**
* **Spring Boot 3.4.3** (Web)
* **Firebase Admin SDK** (9.4.3)
* **Maven** for dependency management

## 📋 Prerequisites

Before you begin, ensure you have met the following requirements:
* You have installed **Java 17** or newer.
* You have installed **Maven**.
* You have a **Firebase Service Account Key**.

## 🛠️ Setup & Installation

1. **Clone the repository:**
   ```bash
   git clone <YOUR_GITHUB_REPO_URL>
   cd scheduleTournament-backend
   ```

2. **Configure Firebase Credentials:**
   The application requires a Firebase Service Account key to connect to the database.
   * Go to the [Firebase Console](https://console.firebase.google.com/).
   * Navigate to **Project settings > Service accounts**.
   * Click **Generate new private key** and save the JSON file.
   * Rename the downloaded file to `serviceAccountKey.json`.
   * Place the file in the `src/main/resources/` directory.

   > ⚠️ **IMPORTANT**: The `serviceAccountKey.json` contains sensitive credentials. It has been added to `.gitignore` to prevent it from being accidentally committed to the repository. **NEVER** share this file or push it to version control!

3. **Configure application properties (optional):**
   The basic configuration is located in `src/main/resources/application.properties`. It runs on port `8080` by default.

## 🏃‍♂️ Running the Application

To run the application locally, you can use the Maven wrapper or your local Maven installation:

```bash
mvn spring-boot:run
```

The server will start at: `http://localhost:8080`

## 📦 Build for Production

To package the application into an executable JAR file, run:

```bash
mvn clean package
```

The JAR file will be generated in the `target/` directory. You can run it using:

```bash
java -jar target/scheduleTournament-api-0.0.1-SNAPSHOT.jar
```

## 🐳 Docker (Optional)

If you want to containerize the application, you can use the provided `Dockerfile` (make sure your `serviceAccountKey.json` is correctly passed or mapped into the container context if required).

## 🗂️ Project Structure

* `controller/` - REST API endpoints (e.g., `TournamentController`, `TourneyDataController`).
* `service/` - Business logic and interaction with Firebase.
* `repository/` - Data access layer interfaces/classes.
* `model/` - Data models/entities (Tournament, Game, Team, Standings, Location).
* `config/` - Configuration classes (CORS, Firebase initialization).
* `exception/` - Custom exceptions and global exception handler.

---
*Created for the Schedule Tournament App.*
