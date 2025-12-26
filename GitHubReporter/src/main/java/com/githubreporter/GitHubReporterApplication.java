package com.githubreporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GitHubReporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(GitHubReporterApplication.class, args);
    }

}
