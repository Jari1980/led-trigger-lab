package se.ledtriggerlab.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.BindingName;

import se.ledtriggerlab.services.ExperimentResultReader;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class ExperimentResultsHttpFunction {

    private final ExperimentResultReader resultReader =
            new ExperimentResultReader();

    @FunctionName("ExperimentResultsHttpFunction")
    public HttpResponseMessage run(

            @HttpTrigger(
                    name = "request",
                    methods = {HttpMethod.GET},
                    route = "experiments/{experimentId}/results",
                    authLevel = AuthorizationLevel.ANONYMOUS
            )
            HttpRequestMessage<Optional<String>> request,

            @BindingName("experimentId") String experimentId,

            final ExecutionContext context) {

        Logger logger = context.getLogger();

        if (experimentId == null || experimentId.isBlank()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Missing experimentId")
                    .build();
        }

        try {
            List<Map<String, Object>> results =
                    resultReader.getResults(experimentId);

            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(results)
                    .build();

        } catch (Exception e) {

            logger.severe(
                    "Failed to retrieve experiment results: "
                            + e.getMessage()
            );

            return request.createResponseBuilder(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body("Failed to retrieve experiment results")
                    .build();
        }
    }
}
