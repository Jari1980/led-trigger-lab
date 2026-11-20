# Build stage
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /workspace

COPY . .

RUN mvn -B clean package

# Runtime stage
FROM mcr.microsoft.com/azure-functions/java:4-java25

ENV AzureWebJobsScriptRoot=/home/site/wwwroot \
    AzureFunctionsJobHost__Logging__Console__IsEnabled=true

COPY --from=build /workspace/target/azure-functions/led-api/ /home/site/wwwroot/

# Local settings must be supplied through the container environment
RUN rm -f /home/site/wwwroot/local.settings.json