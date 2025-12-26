package com.githubreporter.infrastructure.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubStepDto(String name,
                            String status,
                            String conclusion,
                            @JsonProperty("started_at") String startedAt,
                            @JsonProperty("completed_at") String completedAt) {
}
