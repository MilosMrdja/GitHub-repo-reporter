package com.githubreporter.infrastructure.out;

import com.githubreporter.application.usecases.OutputReporter;
import com.githubreporter.domain.WorkflowEvent;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class StdoutReporter implements OutputReporter {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public void reportEvent(WorkflowEvent event) {
        String timestamp = event.timestamp().format(TIME_FORMAT);

        String output = String.format(
                "[%s] | %s | %s (%s @ %s) | %s | %s | %s | %s",
                timestamp,
                event.repository(),
                event.workflowName(),
                event.branch(),
                event.commitSha(), // SHA
                event.eventType(),
                event.jobName() != null ? event.jobName() : "-",
                event.stepName() != null ? event.stepName() : "-",
                event.status() != null ? event.status() : "No step-status"
        );
        System.out.println(output);
    }

    @Override
    public void reportInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    @Override
    public void reportError(String message) {
        System.err.println("[ERROR] " + message);
    }

    @Override
    public void reportDebug(String message) {
        System.out.println("[DEBUG] " + message);
    }
}
