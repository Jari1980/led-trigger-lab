package se.ledtriggerlab.functions;

import se.ledtriggerlab.config.QueueConfiguration;
import se.ledtriggerlab.services.LedEventPublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.time.Instant;

/**
 * Azure Functions with HTTP Trigger.
 */
public class LedHttpFunction {
    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper();

    @FunctionName("LedHttpFunction")
    public HttpResponseMessage run(

            @HttpTrigger(
                    name = "request",
                    methods = {HttpMethod.POST},
                    route = "led",
                    authLevel = AuthorizationLevel.ANONYMOUS
            )
            HttpRequestMessage<Map<String, Object>> request,

            final ExecutionContext context) {

        Logger logger = context.getLogger();

        Map<String, Object> body = request.getBody();

        if (body == null || !body.containsKey("ledId")) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Missing ledId")
                    .build();
        }

        Object ledIdValue = body.get("ledId");

        if (!(ledIdValue instanceof Number)) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("ledId must be a number")
                    .build();
        }

        int ledId = ((Number) ledIdValue).intValue();

        if (ledId < 1 || ledId > 3) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("ledId must be between 1 and 3")
                    .build();
        }

        String eventId = UUID.randomUUID().toString();

        String experimentId =
                (String) body.get("experimentId");

        if (experimentId == null || experimentId.isBlank()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Missing experimentId")
                    .build();
        }

        String publishedAt = Instant.now().toString();

        Map<String, Object> event = Map.of(
                "eventId", eventId,
                "experimentId", experimentId,
                "ledId", ledId,
                "publishedAt", publishedAt
        );

        try {
            String eventMessage =
                    OBJECT_MAPPER.writeValueAsString(event);

            QueueConfiguration configuration =
                    new QueueConfiguration();

            LedEventPublisher publisher =
                    new LedEventPublisher(
                            configuration.getLedQueueClient(),
                            configuration.getTimerQueueClient()
                    );

            publisher.publish(eventMessage);

            logger.info("LED event published to both queues: " + eventId);

            Map<String, Object> response = Map.of(
                    "eventId", eventId,
                    "ledId", ledId,
                    "status", "QUEUED",
                    "message", "LED command accepted"
            );

            return request.createResponseBuilder(HttpStatus.ACCEPTED)
                    .header("Content-Type", "application/json")
                    .body(response)
                    .build();

        } catch (Exception e) {

            logger.severe(
                    "Failed to publish LED event: " + e.getMessage()
            );

            return request.createResponseBuilder(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body("Failed to publish LED event")
                    .build();
        }
    }
}
