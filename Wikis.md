# RuneWars Wiki

Welcome to the RuneWars project Wiki\! This page provides in-depth information about the game's architecture, core mechanics, development setup, and more.

## Table of Contents

1.  [Project Overview](#1-project-overview)
2.  [Architecture Deep Dive](#2-architecture-deep-dive)
      * [Backend Architecture](#backend-architecture)
      * [Frontend Architecture](#frontend-architecture)
      * [Communication Flow](#communication-flow)
3.  [Core Game Mechanics Explained](#3-core-game-mechanics-explained)
      * [Turn Order & Speed](#turn-order--speed)
      * [Skill Point Economy](#skill-point-economy)
      * [Character Attributes & Formulas](#character-attributes--formulas)
      * [Buffs & Debuffs](#buffs--debuffs)
4.  [API Endpoints Reference](#4-api-endpoints-reference)
5.  [Database Schema Overview](#5-database-schema-overview)
6.  [Development Workflow & Contribution](#6-development-workflow--contribution)
7.  [Common Issues & Troubleshooting](#7-common-issues--troubleshooting)

-----

## 1\. Project Overview

RuneWars is a strategic turn-based RPG emphasizing team dynamics and resource management. Players control unique characters across two opposing teams, engaging in tactical combat driven by character speed and a shared team skill point pool. The game is developed with a robust Spring Boot backend for game logic and state management, and a rich Angular GUI for an intuitive player experience.

## 2\. Architecture Deep Dive

RuneWars follows a client-server architecture, with a clear separation of concerns between the backend game logic and the frontend user interface.

### Backend Architecture

The backend is built with **Spring Boot** and **Java**, acting as the authoritative source for all game state and rules.

  * **RESTful API:** Exposes endpoints for user authentication, character actions, game state updates, and more.
  * **Spring Security:** Handles user registration, login, JWT-based authentication, and password reset functionalities.
  * **Game Engine Core:** Manages turn order, combat calculations (damage, healing, shielding), skill point generation/consumption, and the application/removal of buffs/debuffs. This core ensures fair play and consistent game state.
  * **Database Integration:** Uses **JPA/Hibernate** for ORM, interacting with a **MySQL/PostgreSQL** database to persist user accounts, character data, game sessions, and battle logs.
  * **Email Service (SendGrid):** Integrated for transactional emails, primarily for password reset functionalities.

### Frontend Architecture

The frontend is an **Angular** Single Page Application (SPA) built with **TypeScript**, HTML, and CSS.

  * **Component-Based:** The UI is composed of reusable Angular components (e.g., battle arena, character display, skill panel, login/registration forms).
  * **Reactive Forms:** Used for user input and form validation (e.g., login, registration, password reset).
  * **Services:** Angular services handle communication with the backend API (`HttpClient`) and manage shared state (e.g., user session, character data during combat).
  * **Routing:** Angular Router manages navigation between different views (login, character selection, battle).
  * **GUI Focus:** Provides a highly interactive graphical interface, abstracting the complex backend logic from the user.

### Communication Flow

The frontend communicates with the backend primarily via **RESTful API calls over HTTP/HTTPS**.

1.  **Authentication:** User logs in/registers via frontend, sends credentials to backend. Backend returns JWT.
2.  **Authorization:** Frontend includes JWT in `Authorization` header (`Bearer <token>`) for subsequent requests.
3.  **Game Actions:** When a player performs an action (e.g., basic attack, use skill), the frontend sends a POST request to a specific backend endpoint.
4.  **State Updates:** The backend processes the action, updates the game state in memory and potentially the database, and returns the updated state or relevant information (e.g., battle log entries, character health changes) to the frontend.
5.  **Real-time (Future):** While currently request-response, future enhancements might include WebSockets for more real-time battle updates.

## 3\. Core Game Mechanics Explained

### Turn Order & Speed

  * **Initiative:** Characters are ranked by their `Speed` attribute. The character with the highest speed acts first.
  * **Dynamic Order:** After each action, the turn order is recalculated or adjusted based on the current state and any speed-altering effects (buffs/debuffs).

### Skill Point Economy

  * **Shared Pool:** All characters on a team draw from and contribute to a single "Skill Point" pool.
  * **Generation:** Performing a `Basic Attack` successfully grants the team 1 Skill Point.
  * **Consumption:** Using a `Skill` consumes a specific amount of Skill Points from the shared pool (If skill points are zero characters can't use `Skill` and have to generate by using `Basic Attack`).
  * **Cap:** The Skill Point pool cannot exceed 5 points.
  * **Strategic Choice:** Teams must decide whether to save points for powerful skills or to generate points through basic attacks, weighing immediate damage/healing against future potential.

### Character Attributes & Formulas

  * **Health (❤️ & 💖):** Current HP and maximum HP. Damage reduces current HP. Healing restores it up to Max HP.
  * **Attack (💥):** Primary stat for calculating basic attack damage. `Damage = (Attacker's Attack - Defender's Defense) * Skill_Multiplier` (simplified example).
  * **Speed (🏃💨):** Determines turn order. Higher speed means earlier turns.
  * **Defense (🛡️):** Reduces incoming physical/basic attack damage.
  * *Specific formulas for damage calculation, healing effectiveness, and shield values are handled by the backend game engine.*

### Buffs & Debuffs

  * **Application:** Applied by specific character skills.
  * **Duration:** Last for a set number of turns or indefinitely until dispelled.
  * **Effects:** Directly modify character attributes (e.g., Attack Up, Defense Down, Speed Slow).
  * **List Tracking:** Each character maintains a list of active buffs and debuffs.

## 4\. API Endpoints Reference

The backend exposes a comprehensive set of RESTful API endpoints. All endpoints typically reside under `http://localhost:8080/api/`.

### Authentication (`/api/auth`)

  * `POST /api/auth/register`: Register a new user.
      * **Request Body:** `{ "username": "string", "email": "string", "password": "string" }`
      * **Response:** `{ "message": "User registered successfully!" }`
  * `POST /api/auth/login`: Authenticate a user and receive a JWT.
      * **Request Body:** `{ "username": "string", "password": "string" }`
      * **Response:** `{ "token": "jwt_token_string", "user": { ... } }`
  * `POST /api/auth/forgot-password/request`: Request a password reset code.
      * **Request Body:** `{ "email": "string" }`
      * **Response:** `{ "message": "Password reset code sent to your email." }`
  * `POST /api/auth/reset-password-with-code`: Reset password using the received code.
      * **Request Body:** `{ "email": "string", "resetCode": "string", "newPassword": "string" }`
      * **Response:** `{ "message": "Password reset successful." }`

### Character Management (`/api/characters`)

  * `GET /api/characters/all`: Retrieve all available character types/templates.
  * `POST /api/characters/create`: Create a new player character (requires authentication).
  * `GET /api/characters/my`: Retrieve characters owned by the authenticated user.

### Game Sessions (`/api/game`)

  * `POST /api/game/start`: Gets the character in both the teams and makes initial turn order and returns which character plays first.
    * **Request body:** `{"team1": (List of character IDs of team 1), "team2": (List of characterIds of team2)}`
  * `POST /api/game/action/basic`: Submit a character's **BASIC ATTACK** i.e. a single target attack
    * **Request body:** `{"currentCharacterId": (id of current character taking the turn), "targetId": (id of character being targeted), "currentLineup": (hashmap of lineup in order of which they appear in turn order, map of characterId to its snapshot)}`
  * `POST /api/game/action/skill`: Submit a character's **SKILL** i.e. character specific ability
    * **Request body:** `{"currentCharacterId": (id of current character taking the turn), "currentLineup": (hashmap of lineup in order of which they appear in turn order, map of characterId to its snapshot)}`

## 5\. Database Schema Overview

The database (MySQL/PostgreSQL) stores all persistent game data. Key tables include:

  * **`player`**: User authentication details (username, hashed password, email, roles).
  * **`character`**: Stores character templates or instances, including attributes (health, attack, speed, defense), class, and unique skills.
  * **`ability`**: Stores abilities which are mapped to `character` using `One to One` relationship.
  * **`effect`**: Stores buffs and debuffs given by `abilitiy` using a `Many to Many` relationship.
  * **`ability_effects`**: Maps abilities with effects.
  * **`player_characters`**: Maps the characters owned by the player.

*(Detailed schema diagrams can be added as separate wiki pages if needed).*

## 6\. Development Workflow & Contribution

We welcome contributions to RuneWars\! Please follow these guidelines:

  * **Branching Model:** We use a feature-branch workflow.
      * Create your feature branches from `main`.
      * Naming convention: `feature/your-feature-name` or `bugfix/issue-description`.
  * **Code Style:** Adhere to standard Java/Spring Boot conventions for backend and Angular/TypeScript style guide for frontend.
  * **Commits:** Write clear, concise commit messages.
  * **Pull Requests (PRs):**
      * Submit PRs to the `main` branch.
      * Ensure your branch is up-to-date with `main` before submitting.
      * Provide a clear description of changes and tested functionality.
      * All new features and bug fixes should ideally be accompanied by relevant tests.

## 7\. Common Issues & Troubleshooting

  * **Backend Fails to Start:**
      * **`application.properties`:** Double-check your database connection URL, username, and password. Ensure environment variables for `JWT_SECRET` and `SENDGRID_API_KEY` are correctly set in your terminal session.
      * **Database Not Running:** Ensure your MySQL/PostgreSQL instance is active and accessible.
      * **Port Conflict:** If port 8080 (backend) or 4200 (frontend) is already in use, you'll see an error. Change the port in `application.properties` (server.port) or `angular.json` respectively.
  * **"Passwords do not match" on Frontend:**
      * Ensure your `passwordMatchValidator` in `create-password.component.ts` correctly references `newPassword` and `confirmPassword` controls.
      * The error message will only show after you've typed in one of the fields or tried to submit the form.
  * **Frontend Not Connecting to Backend:**
      * Verify the backend is running (`./gradlew bootRun` output).
      * Check your browser's developer console for network errors (e.g., CORS issues, 404 Not Found).
      * Ensure `http://localhost:8080` is the correct backend URL used in your Angular services.