package se.ledtriggerlab.functions;

import se.ledtriggerlab.config.QueueConfiguration;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import java.util.Optional;

public class QueueTestFunction {

    @FunctionName("QueueTestFunction")
    public HttpResponseMessage run(

            @HttpTrigger(
                    name = "request",
                    methods = {HttpMethod.GET},
                    route = "queue-test",
                    authLevel = AuthorizationLevel.ANONYMOUS
            )
            HttpRequestMessage<Optional<String>> request,

            final ExecutionContext context) {

        try {
            QueueConfiguration configuration =
                    new QueueConfiguration();

            configuration.verifyQueues();

            context.getLogger().info(
                    "Both Azure Storage Queues are accessible."
            );

            return request.createResponseBuilder(HttpStatus.OK)
                    .body("Both queues are accessible!")
                    .build();

        } catch (Exception e) {

            context.getLogger().severe(
                    "Queue verification failed: " + e.getMessage()
            );

            return request.createResponseBuilder(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body("Queue verification failed. Check logs.")
                    .build();
        }
    }
}
