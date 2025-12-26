package com.githubreporter.infrastructure.cli;

import com.githubreporter.application.usecases.MonitorWorkflow;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class MonitorCommand implements CommandLineRunner {

    private final MonitorWorkflow monitorService;
    private final ConfigurableApplicationContext context;

    private final CountDownLatch latch = new CountDownLatch(1);

    public MonitorCommand(MonitorWorkflow monitorService, ApplicationContext context) {
        this.monitorService = monitorService;
        this.context = (ConfigurableApplicationContext) context;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: java -jar target/GitHubReporter-0.0.1-SNAPSHOT.jar <owner/repo> <github_token>\nClose by 'CTRL+C'");
            context.close();
            return;
        }

        String repoFullName = args[0];
        String githubToken = args[1];

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[INFO] CLI tool has stopped.");
            latch.countDown();
            monitorService.stopMonitoring();
        }));

        monitorService.startMonitoring(repoFullName, githubToken);

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Program has stopped.");
        } finally {
            context.close();
        }
    }
}
