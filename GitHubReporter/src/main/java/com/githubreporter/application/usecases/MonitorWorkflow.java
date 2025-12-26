package com.githubreporter.application.usecases;

import com.githubreporter.application.ports.StatePersistencePort;
import com.githubreporter.application.ports.WorkflowDataPort;
import com.githubreporter.domain.WorkflowEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class MonitorWorkflow {

    private final WorkflowDataPort workflowDataPort;
    private final StatePersistencePort statePersistencePort;
    private final OutputReporter outputReporter;

    private final AtomicReference<ZonedDateTime> lastReportedTime = new AtomicReference<>();
    private final AtomicBoolean enabled = new AtomicBoolean(false);

    private String repoFullName;
    private String githubToken;

    public MonitorWorkflow(
            WorkflowDataPort workflowDataPort,
            StatePersistencePort statePersistencePort,
            OutputReporter outputReporter) {
        this.workflowDataPort = workflowDataPort;
        this.statePersistencePort = statePersistencePort;
        this.outputReporter = outputReporter;
    }

    public void startMonitoring(String repoFullName, String githubToken) {
        this.repoFullName = repoFullName;
        this.githubToken = githubToken;

        ZonedDateTime lastRunTime = statePersistencePort.loadLastRunTimestamp(repoFullName)
                .orElse(ZonedDateTime.now().minusMinutes(1));

        lastReportedTime.set(lastRunTime);
        enabled.set(true);

        outputReporter.reportInfo("Monitoring " + repoFullName + " has started.");
        outputReporter.reportInfo("Finding events from: " + lastRunTime);

        monitor();
    }

    public void stopMonitoring() {}

    @Scheduled(fixedRateString = "${monitor.poll-interval-ms}")
    public void monitor() {
        if(!enabled.get()) {return;}
        if (repoFullName == null || githubToken == null) {
            return;
        }

        ZonedDateTime currentStartTime = ZonedDateTime.now();
        ZonedDateTime since = lastReportedTime.get();

        try {
            List<WorkflowEvent> newEvents = workflowDataPort.fetchEventsSince(repoFullName, since, githubToken);

            newEvents.stream()
                    .sorted((e1, e2) -> e1.timestamp().compareTo(e2.timestamp()))
                    .forEach(outputReporter::reportEvent);

            if (!newEvents.isEmpty()) {
                ZonedDateTime latestEventTime = newEvents.stream()
                        .map(WorkflowEvent::timestamp)
                        .max(ZonedDateTime::compareTo)
                        .orElse(currentStartTime);

                lastReportedTime.set(latestEventTime);
                statePersistencePort.saveLastRunTimestamp(repoFullName, latestEventTime);
            }

            outputReporter.reportDebug("Successfully scanned " + newEvents.size() + " new events.");

        } catch (Exception e) {
            outputReporter.reportError("Error occurred: " + e.getMessage());
        }
    }
}
