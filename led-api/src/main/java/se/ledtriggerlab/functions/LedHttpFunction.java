package se.ledtriggerlab.functions;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Azure Functions with HTTP Trigger.
 */
public class LedHttpFunction {
    /**

     */
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

        logger.info("LED command received: " + ledId);

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
    }
}
