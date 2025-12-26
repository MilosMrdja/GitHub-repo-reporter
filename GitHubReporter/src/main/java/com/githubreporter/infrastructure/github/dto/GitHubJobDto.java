package com.githubreporter.infrastructure.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GitHubJobDto(long id,
                           String name,
                           String status,
                           String conclusion,
                           @JsonProperty("started_at") String startedAt,
                           @JsonProperty("completed_at") String completedAt,
                           List<GitHubStepDto> steps) {
}
