package com.githubreporter.application.ports;

import com.githubreporter.domain.WorkflowEvent;

import java.time.ZonedDateTime;
import java.util.List;

public interface WorkflowDataPort {
    List<WorkflowEvent> fetchEventsSince(String repoFullName, ZonedDateTime since, String token);
}
