package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findByStatusAndParentTaskIsNull(String status) {
        return taskRepository.findByStatusAndParentTaskIsNull(status);
    }

    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findByStatus(status);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Task not found"));
    }

    public void updateStatus(Long taskId, String newStatus) {
        Task task = getTaskById(taskId);
        task.setStatus(newStatus);
        taskRepository.save(task);
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }
}
