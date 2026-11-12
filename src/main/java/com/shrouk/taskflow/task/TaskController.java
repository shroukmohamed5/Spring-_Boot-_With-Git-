package com.shrouk.taskflow.task;

import com.shrouk.taskflow.task.TaskDtos.StatusRequest;
import com.shrouk.taskflow.task.TaskDtos.TaskRequest;
import com.shrouk.taskflow.task.TaskDtos.TaskResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@AuthenticationPrincipal UserDetails user,
                               @Valid @RequestBody TaskRequest request) {
        return service.create(user.getUsername(), request);
    }

    @GetMapping
    public Page<TaskResponse> list(@AuthenticationPrincipal UserDetails user,
                                   @RequestParam(required = false) TaskStatus status,
                                   @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                                   Pageable pageable) {
        return service.list(user.getUsername(), status, pageable);
    }

    @GetMapping("/{id}")
    public TaskResponse get(@AuthenticationPrincipal UserDetails user, @PathVariable Long id) {
        return service.get(user.getUsername(), id);
    }

    @PutMapping("/{id}")
    public TaskResponse update(@AuthenticationPrincipal UserDetails user, @PathVariable Long id,
                               @Valid @RequestBody TaskRequest request) {
        return service.update(user.getUsername(), id, request);
    }

    @PatchMapping("/{id}/status")
    public TaskResponse changeStatus(@AuthenticationPrincipal UserDetails user, @PathVariable Long id,
                                     @Valid @RequestBody StatusRequest request) {
        return service.changeStatus(user.getUsername(), id, request.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal UserDetails user, @PathVariable Long id) {
        service.delete(user.getUsername(), id);
    }
}
