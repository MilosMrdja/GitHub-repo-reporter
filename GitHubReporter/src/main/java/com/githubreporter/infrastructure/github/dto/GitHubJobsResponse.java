package com.githubreporter.infrastructure.github.dto;

import java.util.List;

public record GitHubJobsResponse(
        List<GitHubJobDto> jobs
) {}
