package com.githubreporter.infrastructure.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubRunDto(long id,
                           String name,
                           String status,
                           String conclusion,
                           @JsonProperty("created_at") String createdAt,
                           @JsonProperty("updated_at") String updatedAt,
                           @JsonProperty("head_branch") String branch,
                           @JsonProperty("head_sha") String sha) {
}
