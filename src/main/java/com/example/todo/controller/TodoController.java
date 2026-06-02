package com.example.todo.controller;

import com.example.todo.dto.ApiResponse;
import com.example.todo.dto.CloseOrCancelRequest;
import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.model.Priority;
import com.example.todo.model.Status;
import com.example.todo.model.Todo;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Todo>> create(@Valid @RequestBody CreateTodoRequest request) {
        Todo todo = todoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Todo created successfully", todo));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Todo>>> getAll(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority) {

        List<Todo> todos;
        if (status != null) {
            todos = todoService.findByStatus(status);
        } else if (priority != null) {
            todos = todoService.findByPriority(priority);
        } else {
            todos = todoService.findAll();
        }
        return ResponseEntity.ok(ApiResponse.ok("Todos retrieved successfully", todos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Todo>> getById(@PathVariable Long id) {
        return todoService.findById(id)
                .map(todo -> ResponseEntity.ok(ApiResponse.ok("Todo found", todo)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Todo not found with id: " + id)));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<ApiResponse<Todo>> close(
            @PathVariable Long id,
            @Valid @RequestBody CloseOrCancelRequest request) {
        try {
            Todo todo = todoService.close(id, request);
            return ResponseEntity.ok(ApiResponse.ok("Todo closed successfully", todo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Todo>> cancel(
            @PathVariable Long id,
            @Valid @RequestBody CloseOrCancelRequest request) {
        try {
            Todo todo = todoService.cancel(id, request);
            return ResponseEntity.ok(ApiResponse.ok("Todo cancelled successfully", todo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage()));
        }
    }
}
