package com.githubreporter.infrastructure.github.mapper;

import com.githubreporter.domain.WorkflowEvent;
import com.githubreporter.infrastructure.github.dto.GitHubJobDto;
import com.githubreporter.infrastructure.github.dto.GitHubRunDto;
import com.githubreporter.infrastructure.github.dto.GitHubStepDto;

import java.time.ZonedDateTime;

public class GitHubEventMapper {

    public static WorkflowEvent workflowQueued(GitHubRunDto run, String repo) {
        return new WorkflowEvent(
                ZonedDateTime.parse(run.createdAt()),
                repo,
                run.name(),
                null,
                null,
                run.branch(),
                run.sha(),
                WorkflowEvent.EventType.WORKFLOW_QUEUED,
                null
        );
    }

    public static WorkflowEvent jobStarted(GitHubRunDto run, GitHubJobDto job, String repo) {
        return new WorkflowEvent(
                ZonedDateTime.parse(job.startedAt()),
                repo,
                run.name(),
                job.name(),
                null,
                run.branch(),
                run.sha(),
                WorkflowEvent.EventType.JOB_START,
                null
        );
    }

    public static WorkflowEvent jobCompleted(GitHubRunDto run, GitHubJobDto job, String repo) {
        return new WorkflowEvent(
                ZonedDateTime.parse(job.completedAt()),
                repo,
                run.name(),
                job.name(),
                null,
                run.branch(),
                run.sha(),
                WorkflowEvent.EventType.JOB_END,
                null

        );
    }

    public static WorkflowEvent stepStarted(GitHubRunDto run, GitHubJobDto job, GitHubStepDto step, String repo) {
        return new WorkflowEvent(
                ZonedDateTime.parse(step.startedAt()),
                repo,
                run.name(),
                job.name(),
                step.name(),
                run.branch(),
                run.sha(),
                WorkflowEvent.EventType.STEP_START,
                null
        );
    }

    public static WorkflowEvent stepCompleted(GitHubRunDto run, GitHubJobDto job, GitHubStepDto step, String repo) {
        WorkflowEvent.Status conclusion = mapConclusion(step.conclusion());
        return new WorkflowEvent(
                ZonedDateTime.parse(step.completedAt()),
                repo,
                run.name(),
                job.name(),
                step.name(),
                run.branch(),
                run.sha(),
                WorkflowEvent.EventType.STEP_END,
                conclusion
        );
    }

    private static WorkflowEvent.Status mapConclusion(String conclusion) {
        return switch (conclusion.toLowerCase()) {
            case "success" -> WorkflowEvent.Status.SUCCESS;
            case "failure", "cancelled", "timed_out" -> WorkflowEvent.Status.FAILURE;
            case "skipped" -> WorkflowEvent.Status.SKIPPED;
            default -> WorkflowEvent.Status.UNKNOWN;
        };
    }
}
