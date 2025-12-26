package com.githubreporter.infrastructure.state;

import com.githubreporter.application.ports.StatePersistencePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Component
public class FileStateAdapter implements StatePersistencePort {

    private final String stateDir;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public FileStateAdapter(@Value("${monitor.state.dir.path:./workflow_monitor_state}") String stateDir) {
        this.stateDir = stateDir;
    }
    @Override
    public void saveLastRunTimestamp(String repoFullName, ZonedDateTime timestamp) {
        Path filePath = getFilePath(repoFullName);
        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, timestamp.format(FORMATTER));
        } catch (IOException e) {
            System.err.println("Error while reading from " + repoFullName + ": " + e.getMessage());
        }
    }

    @Override
    public Optional<ZonedDateTime> loadLastRunTimestamp(String repoFullName) {
        Path filePath = getFilePath(repoFullName);
        try {
            if (Files.exists(filePath)) {
                String timestampStr = Files.readString(filePath).trim();
                return Optional.of(ZonedDateTime.parse(timestampStr, FORMATTER));
            }
        } catch (IOException | DateTimeParseException e) {
            System.err.println("Error while reading for " + repoFullName + ": " + e.getMessage());
        }
        return Optional.empty();
    }


    private Path getFilePath(String repoFullName) {
        String safeRepoName = repoFullName.replace('/', '_');
        return Path.of(this.stateDir, safeRepoName + ".txt");
    }
}
