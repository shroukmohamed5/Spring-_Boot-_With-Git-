package com.shrouk.taskflow.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

public final class TaskDtos {

    private TaskDtos() {}

    public record TaskRequest(
            @NotBlank @Size(max = 120) String title,
            @Size(max = 2000) String description,
            TaskPriority priority,
            LocalDate dueDate) {}

    public record StatusRequest(@jakarta.validation.constraints.NotNull TaskStatus status) {}

    public record TaskResponse(
            Long id, String title, String description,
            TaskStatus status, TaskPriority priority,
            LocalDate dueDate, Instant createdAt) {

        public static TaskResponse from(Task t) {
            return new TaskResponse(t.getId(), t.getTitle(), t.getDescription(),
                    t.getStatus(), t.getPriority(), t.getDueDate(), t.getCreatedAt());
        }
    }
}
