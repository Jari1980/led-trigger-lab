

# Runtime stage
FROM mcr.microsoft.com/azure-functions/java:4-java25

ENV AzureWebJobsScriptRoot=/home/site/wwwroot \
    AzureFunctionsJobHost__Logging__Console__IsEnabled=true

COPY target/azure-functions/led-queue-engine/ /home/site/wwwroot/

# Local settings must be supplied through the container environment
RUN rm -f /home/site/wwwroot/local.settings.json