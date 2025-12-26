package com.githubreporter.infrastructure.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GitHubRunsResponse(
        @JsonProperty("workflow_runs")
        List<GitHubRunDto> runs
) {}
