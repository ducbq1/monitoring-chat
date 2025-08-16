package com.example.demo.controller;

import com.example.demo.helper.ViewHelper;
import com.example.demo.model.Comment;
import com.example.demo.model.Task;
import com.example.demo.model.TaskColumn;
import com.example.demo.model.User;
import com.example.demo.service.CommentService;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TaskController {

    private final TaskService taskService;
    private final CommentService commentService;
    private final UserService userService;

    public TaskController(TaskService taskService, CommentService commentService, UserService userService) {
        this.taskService = taskService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/tasks")
    public String viewBoard(Model model) {

        List<TaskColumn> columns = new ArrayList<>();

        for (String status : List.of("TODO", "IN_PROGRESS", "DONE")) {
            List<Task> tasks = taskService.findByStatusAndParentTaskIsNull(status);
            columns.add(new TaskColumn(status, tasks));
        }

        model.addAttribute("columns", columns);
        ViewHelper.setView(model, "view/task-board", "Task Manager");
        return "layout";
    }

    @GetMapping("/task/new")
    public String createTaskForm(@RequestParam(required = false) Long parentId, Model model) {
        Task task = new Task();
        if (parentId != null) {
            Task parent = taskService.getTaskById(parentId);
            task.setParentTask(parent);
        }
        model.addAttribute("task", task);
        model.addAttribute("users", userService.findAllByManagerIsNull());
        ViewHelper.setView(model, "view/task-detail", "Task Detail");
        return "layout";
    }

    @PostMapping("/task/save")
    public String saveTask(@RequestParam(required = false) Long id,
                           @RequestParam String title,
                           @RequestParam String description,
                           @RequestParam String status,
                           @RequestParam List<Long> assigneeIds,
                           @RequestParam(required = false) List<Long> watcherIds,
                           @RequestParam(required = false) Long parentId) {

        Task task = (id != null) ? taskService.getTaskById(id) : new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setAssignees(userService.findByIds(assigneeIds));
        task.setWatchers(watcherIds != null ? userService.findByIds(watcherIds) : List.of());

        if (id == null) {
            task.setCreatedAt(LocalDateTime.now());
        }

        // Gán task cha nếu có
        if (parentId != null) {
            Task parentTask = taskService.getTaskById(parentId);
            task.setParentTask(parentTask);
        } else {
            task.setParentTask(null);
        }

        taskService.save(task);
        return "redirect:/tasks";
    }


    @GetMapping("/task/{id}")
    public String viewTaskDetail(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskById(id);
        List<Comment> comments = commentService.findByTaskId(id);
        List<User> users = userService.findAllByManagerIsNull();

        model.addAttribute("task", task);
        model.addAttribute("users", users);
        model.addAttribute("comments", comments);
        ViewHelper.setView(model, "view/task-detail", "Task Detail");
        return "layout";
    }

    @PostMapping("/task/{id}/change-status")
    @ResponseBody
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        taskService.updateStatus(id, newStatus);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/{id}/comment")
    public String addComment(@PathVariable Long id,
                             @RequestParam String content,
                             Principal principal) {
        String author = principal != null ? principal.getName() : "Guest";
        commentService.addComment(id, author, content);
        return "redirect:/task/" + id;
    }
}
