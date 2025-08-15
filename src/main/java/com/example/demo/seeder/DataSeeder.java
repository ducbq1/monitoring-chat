package com.example.demo.seeder;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final TaskService taskService;
    private final UserService userService;

    public DataSeeder(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        // ✅ Tạo người dùng
        User ceo = new User("ceo", "Trần CEO", null);
        ceo.setAccessKey("1111");
        User manager1 = new User("manager1", "Nguyễn Quản Lý 1", ceo);
        User manager2 = new User("manager2", "Phạm Quản Lý 2", ceo);

        User staff1 = new User("staff1", "Lê Nhân Viên 1", manager1);
        User staff2 = new User("staff2", "Trịnh Nhân Viên 2", manager1);
        User staff3 = new User("staff3", "Vũ Nhân Viên 3", manager2);
        User staff4 = new User("staff4", "Đỗ Nhân Viên 4", manager2);

        User intern1 = new User("intern1", "Bùi Thực Tập 1", staff1);
        User intern2 = new User("intern2", "Hoàng Thực Tập 2", staff2);
        User intern3 = new User("intern3", "Ngô Thực Tập 3", staff3);

        List<User> users = List.of(ceo, manager1, manager2, staff1, staff2, staff3, staff4, intern1, intern2, intern3);
        users.forEach(userService::save);

        // ✅ Tạo task với danh sách assignee + watcher hợp lý
        taskService.save(new Task(
                "Fix login bug",
                "Null pointer khi login",
                "TODO",
                List.of(staff1),
                List.of(manager1, ceo)
        ));

        taskService.save(new Task(
                "Triển khai Kafka",
                "Triển khai cho hệ thống",
                "IN_PROGRESS",
                List.of(staff2, staff3),
                List.of(manager2, ceo)
        ));

        taskService.save(new Task(
                "Viết tài liệu",
                "Thêm hướng dẫn sử dụng",
                "DONE",
                List.of(intern1, intern2),
                List.of(staff1, manager1)
        ));
    }
}
