package com.githubreporter.application.ports;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface StatePersistencePort {
    void saveLastRunTimestamp(String repoFullName, ZonedDateTime timestamp);

    Optional<ZonedDateTime> loadLastRunTimestamp(String repoFullName);
}
