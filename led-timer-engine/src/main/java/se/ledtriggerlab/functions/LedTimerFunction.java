package se.ledtriggerlab.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import se.ledtriggerlab.services.LedQueueReader;

public class LedTimerFunction {

    private final LedQueueReader queueReader =
            new LedQueueReader();

    @FunctionName("LedTimerFunction")
    public void run(
            @TimerTrigger(
                    name = "timerInfo",
                    schedule = "*/5 * * * * *"
            ) String timerInfo,
            final ExecutionContext context) {

        context.getLogger().info(
                "Timer Trigger started"
        );

        queueReader.processNextMessage(context);
    }
}
