package com.githubreporter.infrastructure.github;

import com.githubreporter.application.ports.WorkflowDataPort;
import com.githubreporter.domain.WorkflowEvent;
import com.githubreporter.infrastructure.github.dto.*;
import com.githubreporter.infrastructure.github.mapper.GitHubEventMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class GitHubApiAdapter implements WorkflowDataPort {

    private final RestTemplate restTemplate;

    public GitHubApiAdapter(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }


    @Override
    public List<WorkflowEvent> fetchEventsSince(String repoFullName, ZonedDateTime since, String token) {
        List<WorkflowEvent> events = new ArrayList<>();

        String url = String.format("https://api.github.com/repos/%s/actions/runs?created=>%s&status=completed&per_page=100",
                repoFullName, since.toInstant().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<GitHubRunsResponse> runsResponse =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        GitHubRunsResponse.class
                );

        if (runsResponse.getBody() == null) {
            return events;
        }

        for(GitHubRunDto run : runsResponse.getBody().runs()){
            ZonedDateTime runCreated =
                    ZonedDateTime.parse(run.createdAt());

            if (runCreated.isBefore(since)) {
                continue;
            }
            events.add(GitHubEventMapper.workflowQueued(run, repoFullName));

            String jobsUrl = String.format(
                    "https://api.github.com/repos/%s/actions/runs/%d/jobs",
                    repoFullName,
                    run.id()
            );

            ResponseEntity<GitHubJobsResponse> jobsResponse =
                    restTemplate.exchange(
                            jobsUrl,
                            HttpMethod.GET,
                            entity,
                            GitHubJobsResponse.class
                    );

            if (jobsResponse.getBody() == null) continue;

            for (GitHubJobDto job : jobsResponse.getBody().jobs()) {

                if (job.startedAt() != null &&
                        ZonedDateTime.parse(job.startedAt()).isAfter(since)) {
                    events.add(GitHubEventMapper.jobStarted(run, job, repoFullName));
                }

                if (job.completedAt() != null &&
                        ZonedDateTime.parse(job.completedAt()).isAfter(since)) {
                    events.add(GitHubEventMapper.jobCompleted(run, job, repoFullName));
                }

                if (job.steps() == null) continue;

                for (GitHubStepDto step : job.steps()) {

                    if (step.startedAt() != null &&
                            ZonedDateTime.parse(step.startedAt()).isAfter(since)) {
                        events.add(GitHubEventMapper.stepStarted(run, job, step, repoFullName));
                    }

                    if (step.completedAt() != null &&
                            ZonedDateTime.parse(step.completedAt()).isAfter(since)) {
                        events.add(GitHubEventMapper.stepCompleted(run, job, step, repoFullName));
                    }
                }
            }
        }

        return events;
    }
}
