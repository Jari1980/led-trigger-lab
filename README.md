# LedTriggerLab

## Azure Queue Trigger vs Timer Trigger

LedTriggerLab is an experimental project exploring two different approaches to asynchronous event processing using Azure Functions.

The project compares:

- **Queue Trigger** – Processes events when messages arrive in an Azure Storage Queue.
- **Timer Trigger** – Periodically checks an Azure Storage Queue and processes available messages.

The purpose is to demonstrate the architectural differences between event-driven and scheduled message processing.

## Architecture

The application consists of three Azure Functions applications and a React frontend.

### Components

| Component | Responsibility |
|---|---|
| React Frontend | Provides experiment controls, allows users to generate LED events and displays processing results. |
| LED API | HTTP Trigger Functions that receive LED events through POST requests, publish messages to Azure Storage Queues, and retrieve experiment results from Azure Table Storage through GET requests. |
| LED Queue Engine | Processes events using Azure Queue Trigger. |
| LED Timer Engine | Uses Timer Trigger to periodically check and process messages from Azure Storage Queue. |
| Azure Storage | Provides queues for event processing and table storage for experiment results. |
| Azurite | Local Azure Storage emulator. |

### Event processing flow

1. The user selects an LED in the React frontend.
2. The frontend sends an HTTP POST request to the LED API.
3. The LED API validates the request and creates an event with a unique event ID.
4. The event is published to both Azure Storage Queues.
5. The Queue Engine processes messages automatically through Azure Queue Trigger.
6. The Timer Engine periodically checks its queue and processes available messages.
7. Both engines record their processing results in Azure Table Storage.
8. The frontend periodically sends HTTP GET requests to the LED API to retrieve the latest experiment results.
9. The LED API reads the results from Azure Table Storage and returns them to the frontend as JSON.
10. The frontend displays the processing results for both pipelines.

## Technology stack

- Java 25
- Azure Functions v4
- Azure Storage Queues
- Azure Table Storage
- React
- TypeScript
- Docker / Docker Compose (experimental)
- Azurite

## Running locally

The recommended development environment is local execution using Maven and Azure Functions Core Tools.

Docker Compose is provided as an experimental alternative and is not the recommended environment for performance testing.

### Local development setup

To run LedTriggerLab locally, you need:

- Java 25
- Maven
- Azure Functions Core Tools v4
- Node.js and npm
- Azurite
- Microsoft Azure Storage Explorer (recommended)

### Azurite – Local Azure Storage Emulator

LedTriggerLab uses Azure Storage Queues and Azure Table Storage.

Azurite provides a local Azure Storage emulator, allowing the application to run without an Azure subscription.

Install Azurite:

```bash
npm install -g azurite
```
Start Azurite from the project root:
```bash
azurite --location .azurite --debug .azurite/debug.log --silent
```
Azurite provides the following local storage services:

Service

Endpoint

Blob Storage

http://127.0.0.1:10000 

Queue Storage

http://127.0.0.1:10001 

Table Storage

http://127.0.0.1:10002 

The .azurite directory contains the local storage data and should be preserved between development sessions.

Microsoft Azure Storage Explorer
Microsoft Azure Storage Explorer is recommended for inspecting and managing local storage.

Connect to the local Azurite emulator using the Storage Explorer interface.

Storage Explorer allows you to:

- Inspect Azure Storage Queues.

- View messages in queues.

- Inspect Azure Tables.

- View experiment result entities.

- Verify that messages are published and processed correctly.

### Image of UI and storage explorer

<img width="2239" height="1044" alt="LedTriggerLab Image processing triggers" src="https://github.com/user-attachments/assets/23021c26-f4d9-460a-ad07-64a4adf50ded" />

