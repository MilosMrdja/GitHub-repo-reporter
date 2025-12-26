package com.githubreporter.domain;

import java.time.ZonedDateTime;

public record WorkflowEvent(
        ZonedDateTime timestamp,
        String repository, // owner/repo
        String workflowName,
        String jobName,
        String stepName,
        String branch,
        String commitSha,
        EventType eventType,
        Status status
) {
    public enum EventType {
        WORKFLOW_QUEUED,
        JOB_START,
        JOB_END,
        STEP_START,
        STEP_END
    }

    public enum Status {
        SUCCESS,
        FAILURE,
        SKIPPED,
        UNKNOWN
    }

}
