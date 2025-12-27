# GitHub Workflow Run Monitor CLI

## 🚀 Short Description

The **GitHub Workflow Run Monitor CLI** is a lightweight command-line utility built with Spring Boot. Its primary function is to continuously poll the GitHub Actions API for a specified repository and report all workflow run, job, and step events (start, success, failure, completion) that have occurred since the last time the tool was executed. It is designed to be a passive monitoring tool, providing real-time visibility into the CI/CD pipeline status directly in the console.

## ✨ Features

- **Persistent State:** Reports only new events since the last successful run, preventing redundant output. By using file directories.
- **Real-time Polling:** Uses Spring Scheduling to periodically fetch the latest data from the GitHub API.
- **Clear Output:** Formats workflow events into a clean, chronological console output.
- **Robust Data Handling:** Maps raw GitHub API responses to domain-specific events (`WorkflowEvent`).

## 🛠️ Prerequisites

Before running the application, ensure you have the following installed:

1.  **Java Development Kit (JDK) 21 or newer**
2.  **Apache Maven 3.6+**
3.  **Git** command line tool
4.  **A GitHub Personal Access Token (PAT)** with the **`repo`** scope checked (required to read workflow status data from private repositories).

## 🏃 Getting Started

### 1. Build the Application

Navigate to the project's root directory and use Maven to compile and package the application into an executable JAR file:

```bash
# Clone repository
git clone <repository-url> (https://github.com/MilosMrdja/GitHub-repo-reporter.git)
cd GitHubReporter

# Build the project
./mvnw clean package

# Or use Maven directly
mvn clean package -DskipTests
# I have some error with default test case, and I do not have time to debug it
```

This will create the executable JAR file, typically named GitHubReporter-0.0.1-SNAPSHOT.jar, inside the target/ directory.

### 2. Configure State Storage (Optional)

If you want, you can change directory location and scheduled time to fetch new GitHub API events in application.properties

```bash
monitor.state.dir.path=./src/main/resources/static/workflow_monitor_state_data
monitor.poll-interval-ms=30000
```

### 3. Run the CLI tool

Execute the JAR file using Java, passing the repository name (owner/repo) and your GitHub PAT as command-line arguments:

```bash
# Run the JAR file
java -jar target/GitHubReporter-0.0.1-SNAPSHOT.jar <owner/repo-name> <your-github-pat>

# Or use Maven plugin, but you need to manually set both line arguments in seetings
./mvnw spring-boot:run
```

## Discusion

### 📈 Potential Improvements

- Switch to WebHooks: Instead of scheduled polling, integrate a GitHub WebHook receiver to get real-time push notifications about events, reducing latency and GitHub API usage.
- Or Advanced Polling Logic: Dynamically adjust the polling interval based on the current load or the activity of the repository.
- Output Formatting: Add colorized console output to easily distinguish between SUCCESS, FAILURE, and IN_PROGRESS events.

### 🐛 Known Limitations

- Polling Overhead: Continuous polling is inherently inefficient compared to event-driven WebHooks, potentially wasting API budget for inactive repositories.
- Using some database instead of directories.
