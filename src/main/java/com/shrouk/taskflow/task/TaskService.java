package com.shrouk.taskflow.task;

import com.shrouk.taskflow.exception.NotFoundException;
import com.shrouk.taskflow.task.TaskDtos.TaskRequest;
import com.shrouk.taskflow.task.TaskDtos.TaskResponse;
import com.shrouk.taskflow.user.User;
import com.shrouk.taskflow.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {

    private final TaskRepository tasks;
    private final UserRepository users;

    public TaskService(TaskRepository tasks, UserRepository users) {
        this.tasks = tasks;
        this.users = users;
    }

    public TaskResponse create(String username, TaskRequest req) {
        User owner = users.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Task task = new Task(req.title(), req.description(), req.priority(), req.dueDate(), owner);
        return TaskResponse.from(tasks.save(task));
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> list(String username, TaskStatus status, Pageable pageable) {
        Page<Task> page = (status == null)
                ? tasks.findByOwnerUsername(username, pageable)
                : tasks.findByOwnerUsernameAndStatus(username, status, pageable);
        return page.map(TaskResponse::from);
    }

    @Transactional(readOnly = true)
    public TaskResponse get(String username, Long id) {
        return TaskResponse.from(findOwned(username, id));
    }

    public TaskResponse update(String username, Long id, TaskRequest req) {
        Task task = findOwned(username, id);
        task.setTitle(req.title());
        task.setDescription(req.description());
        if (req.priority() != null) task.setPriority(req.priority());
        task.setDueDate(req.dueDate());
        return TaskResponse.from(task);
    }

    public TaskResponse changeStatus(String username, Long id, TaskStatus status) {
        Task task = findOwned(username, id);
        task.setStatus(status);
        return TaskResponse.from(task);
    }

    public void delete(String username, Long id) {
        tasks.delete(findOwned(username, id));
    }

    private Task findOwned(String username, Long id) {
        return tasks.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new NotFoundException("Task " + id + " not found"));
    }
}
