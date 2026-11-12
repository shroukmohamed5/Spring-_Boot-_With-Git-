package com.shrouk.taskflow.task;

import com.shrouk.taskflow.exception.NotFoundException;
import com.shrouk.taskflow.task.TaskDtos.TaskRequest;
import com.shrouk.taskflow.task.TaskDtos.TaskResponse;
import com.shrouk.taskflow.user.User;
import com.shrouk.taskflow.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock TaskRepository tasks;
    @Mock UserRepository users;
    @InjectMocks TaskService service;

    @Test
    void createSavesTaskForOwner() {
        User owner = new User("shrouk", "s@example.com", "hash");
        when(users.findByUsername("shrouk")).thenReturn(Optional.of(owner));
        when(tasks.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse res = service.create("shrouk",
                new TaskRequest("Write tests", "JUnit + Mockito", TaskPriority.HIGH, null));

        assertThat(res.title()).isEqualTo("Write tests");
        assertThat(res.priority()).isEqualTo(TaskPriority.HIGH);
        assertThat(res.status()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    void changeStatusUpdatesTask() {
        User owner = new User("shrouk", "s@example.com", "hash");
        Task task = new Task("Deploy", null, null, null, owner);
        when(tasks.findByIdAndOwnerUsername(1L, "shrouk")).thenReturn(Optional.of(task));

        TaskResponse res = service.changeStatus("shrouk", 1L, TaskStatus.DONE);

        assertThat(res.status()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void getThrowsWhenTaskBelongsToSomeoneElse() {
        when(tasks.findByIdAndOwnerUsername(5L, "shrouk")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get("shrouk", 5L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteRemovesOwnedTask() {
        Task task = new Task("Old", null, null, null, new User("shrouk", "s@example.com", "hash"));
        when(tasks.findByIdAndOwnerUsername(2L, "shrouk")).thenReturn(Optional.of(task));

        service.delete("shrouk", 2L);

        verify(tasks).delete(task);
    }
}
