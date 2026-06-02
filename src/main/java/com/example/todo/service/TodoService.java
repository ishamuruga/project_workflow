package com.example.todo.service;

import com.example.todo.dto.CloseOrCancelRequest;
import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.model.Priority;
import com.example.todo.model.Status;
import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo create(CreateTodoRequest request) {
        Todo todo = new Todo(request.getTitle(), request.getDescription(), request.getPriority());
        return todoRepository.save(todo);
    }

    @Transactional(readOnly = true)
    public List<Todo> findAll() {
        return todoRepository.findAllByOrderByPriorityDescCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Optional<Todo> findById(Long id) {
        return todoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Todo> findByStatus(Status status) {
        return todoRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Todo> findByPriority(Priority priority) {
        return todoRepository.findByPriority(priority);
    }

    public Todo close(Long id, CloseOrCancelRequest request) {
        Todo todo = getOpenTodo(id);
        todo.setStatus(Status.CLOSED);
        todo.setRemarks(request.getRemarks());
        return todoRepository.save(todo);
    }

    public Todo cancel(Long id, CloseOrCancelRequest request) {
        Todo todo = getOpenTodo(id);
        todo.setStatus(Status.CANCELLED);
        todo.setRemarks(request.getRemarks());
        return todoRepository.save(todo);
    }

    private Todo getOpenTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found with id: " + id));
        if (todo.getStatus() != Status.OPEN) {
            throw new IllegalStateException("Todo is already " + todo.getStatus() + " and cannot be modified");
        }
        return todo;
    }
}
