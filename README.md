# RuneWars 🎮

## A Strategic Turn-Based RPG

RuneWars is a strategic turn-based role-playing game where two opposing teams clash in tactical combat. Gameplay revolves around characters taking turns based on their "speed" stat, and teams sharing a common "skill point" pool to utilize powerful special abilities. Master resource management and character synergies to lead your team to victory\!

## Table of Contents

  * [Core Concept](#core-concept)
  * [Key Features](#key-features)
  * [What's New](#whats-new)
  * [Technologies Used](#technologies-used)
  * [Getting Started](#getting-started)
      * [Prerequisites](#prerequisites)
      * [Environment Variables for Secrets](#environment-variables-for-secrets)
      * [Running the Backend](#running-the-backend)
      * [Running the Frontend](#running-the-frontend)
  * [Future Plans](#future-plans)

## Core Concept

RuneWars brings classic turn-based RPG combat to life with a focus on team strategy and resource management. Players command a roster of unique characters, each with distinct roles and abilities, to outmaneuver their opponents. The shared "skill point" pool adds a layer of depth, forcing teams to make critical decisions on when and how to unleash their most devastating attacks or life-saving heals.

## Key Features

### Combat System

  * **Turn-Based System:** 🔄 Characters act in sequence, determined by their `speed` stat.
  * **Two Teams:** 🤝 Players are divided into two opposing teams.
  * **Skill Points:** 🌟 A shared resource for teams to activate special skills.
      * **Basic Attack:** 🗡️ A standard attack dealing damage scaled by character stats. Successfully using a basic attack earns the team one skill point.
      * **Skill:** ✨ A unique ability specific to each character, with varying effects. Utilizing a skill consumes skill points from the team's shared pool.
      * **Skill Point Cap:** 上限 5️⃣ The team's skill point pool is capped at 5.
      * **Resource Management:** 🧠 Teams must strategically manage skill points as they are shared amongst all characters.
      * **NOTE:** ⚠️ Skill point pool is common for the entire team. If characters use skill points and the pool becomes 0, then the next character taking a turn cannot use their skill and will be bound to use a basic attack in order to gain one skill point.

### Character System

Each character possesses the following core attributes:

  * **Health:** ❤️ & **Max Health:** 💖
  * **Attack:** 💥
  * **Speed:** 🏃💨
  * **Defense:** 🛡️
  * **List of Buffs and Debuffs:** ⬆️ ⬇️

### Character Classes

Characters are categorized into distinct classes, each with a specialized role:
These are few of the characters, more will be added to make the game intersting.

  * **DPS (Damage Per Second):** 🎯 The main damage dealer, focused on maximizing offensive output.
  * **Healer:** ✚ Primarily responsible for restoring allies' health.
  * **Shielder:** 🛡️ Provides defensive barriers to allies, absorbing incoming damage.
  * **Buffer:** ⬆️ Grants beneficial stat boosts (buffs) to allies.
  * **Debuffer:** ⬇️ Inflicts negative stat reductions (debuffs) on enemies.

## What's New

We've made significant strides in improving the user experience and core gameplay:

1.  **Battle Log:** A real-time log displaying previous character actions during combat.
2.  **Ability Description:** A long-awaited feature allowing users to view detailed descriptions of each character's unique abilities.
3.  **Graphical User Interface (GUI):** A major update transforming the application from a Command Line Interface (CLI) to an intuitive and easy-to-play GUI. The backend continues to manage turn order and the effects of every action.
4.  **Battle Pass:** Players can now choose to opt in or opt out of a Battle Pass system, which could grant them access to more character types.

## Technologies Used

### Backend

  * **Spring Boot:** Framework for building robust, stand-alone, production-grade Spring-based applications.
  * **Java:** Core programming language.
  * **Spring Security:** For authentication and authorization.
  * **Gradle:** Build automation tool.
  * **MySQL/PostgreSQL:** Relational database management systems (configurable).
  * **SendGrid:** Email delivery service (used for features like password reset).

### Frontend

  * **Angular:** Framework for building dynamic single-page applications.
  * **TypeScript:** Superset of JavaScript that adds static typing.
  * **HTML:** Standard markup language for web pages.
  * **CSS:** Stylesheet language for web page design.

## Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

Before you begin, ensure you have the following installed:

  * **Java Development Kit (JDK):** Version 24.
  * **Node.js & npm/yarn:** Node.js (which includes npm) is required for Angular.
      * [Download Node.js](https://nodejs.org/)
  * **Angular CLI:** Install using npm:
    ```bash
    npm install -g @angular/cli
    ```
  * **Database:** A running instance of PostgreSQL. You'll need to configure your `application.properties` to connect to it.
  
  To create the necessary database for the backend, you can use the following command in your db_setup.sh script or directly run the script (assuming you have PostgreSQL installed and configured):
  ```bash
    sudo -u postgres psql -c 'create database webgame;'
    # OR if you want to run the script
    bash db_setup.sh
  ```

### Environment Variables for Secrets

To run the application securely, you must set the following environment variables. These are **confidential** and **should never be committed to your Git repository.**

  * `JWT_SECRET`: A strong, randomly generated string used for JWT token signing.
  * `SENDGRID_API_KEY`: Your SendGrid API key for email services (e.g., password reset).

**On ChromeOS (Linux container):**

1.  Open your Linux terminal.
2.  For a **persistent setup** (recommended), add these lines to your `~/.bashrc` or `~/.zshrc` file:
    ```bash
    # RuneWars Secret Keys
    export JWT_SECRET="your_highly_secret_jwt_key_here"
    export SENDGRID_API_KEY="SG.your_sendgrid_api_key_here"
    ```
    Replace the placeholder values with your actual secret keys.
3.  Save the file and then source it:
    ```bash
    source ~/.bashrc # or source ~/.zshrc
    ```
    New terminals will now automatically have these variables. For your current terminal, run the `export` commands directly if you prefer not to source.

### Running the Backend

1.  Navigate to the `back-end` directory of the project:
    ```bash
    cd final-capstone-project/web-rpg-game/back-end
    ```
2.  Build and run the Spring Boot application using Gradle:
    ```bash
    ./gradlew bootRun
    # OR
    gradle bootRun
    ```
    The backend will typically start on `http://localhost:8080`.

### Running the Frontend

1.  Open a new terminal and navigate to the `front-end` directory (assuming it's a sibling to `back-end`):
    ```bash
    cd final-capstone-project/web-rpg-game/front-end
    # (Adjust path if your frontend is in a different location relative to the root project)
    ```
2.  Install the necessary npm packages:
    ```bash
    npm install
    ```
3.  Start the Angular development server:
    ```bash
    ng serve
    ```
    The frontend will typically be served at `http://localhost:4200`. You can then access the game in your web browser.

## Future Plans

  * Further character balancing and new skill additions.
  * More intricate combat mechanics and status effects.
  * Expansion of Battle Pass content and progression.
  * Persistent player data and game progress saving.
  * Multiplayer features (PvP or Co-op).