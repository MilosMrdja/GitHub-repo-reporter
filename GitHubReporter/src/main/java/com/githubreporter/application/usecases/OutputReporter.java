package com.githubreporter.application.usecases;

import com.githubreporter.domain.WorkflowEvent;

public interface OutputReporter {
    void reportEvent(WorkflowEvent event);
    void reportInfo(String message);
    void reportError(String message);
    void reportDebug(String message);
}
