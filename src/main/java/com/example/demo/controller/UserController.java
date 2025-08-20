package com.example.demo.controller;

import com.example.demo.helper.ViewHelper;
import com.example.demo.model.TreeNode;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String userInfo(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        User freshUser = userRepository.findById(user.getId()).orElse(user);
        model.addAttribute("user", freshUser);
        ViewHelper.setView(model, "view/user-info", "User Information");
        return "layout";
    }

    @GetMapping("/tree")
    public String treeView(Model model) {
        List<TreeNode> treeData = List.of(
                new TreeNode("1", "🏛️ C-Level", List.of(
                        new TreeNode("1-1", "👨‍💼 CTO - Chief Technology Officer", List.of(
                                new TreeNode("1-1-1", "📡 VP of Engineering", List.of(
                                        new TreeNode("1-1-1-1", "🧠 Software Architect", List.of()),
                                        new TreeNode("1-1-1-2", "⚙️ Engineering Manager", List.of(
                                                new TreeNode("1-1-1-2-1", "💻 Senior Backend Engineer", List.of()),
                                                new TreeNode("1-1-1-2-2", "🌐 Senior Frontend Engineer", List.of())
                                        ))
                                )),
                                new TreeNode("1-1-2", "🔐 VP of Infrastructure & Security", List.of(
                                        new TreeNode("1-1-2-1", "☁️ Cloud Architect", List.of()),
                                        new TreeNode("1-1-2-2", "🛡️ DevSecOps Engineer", List.of())
                                ))
                        )),
                        new TreeNode("1-2", "📈 CIO - Chief Information Officer", List.of(
                                new TreeNode("1-2-1", "🧩 Enterprise Architect", List.of()),
                                new TreeNode("1-2-2", "🖥️ IT Operations Manager", List.of())
                        ))
                )),

                new TreeNode("2", "👨‍🔧 Technical Roles", List.of(
                        new TreeNode("2-1", "💻 Backend Developer", List.of(
                                new TreeNode("2-1-1", "Java Developer", List.of()),
                                new TreeNode("2-1-2", "Go Developer", List.of()),
                                new TreeNode("2-1-3", "Python Developer", List.of())
                        )),
                        new TreeNode("2-2", "🌐 Frontend Developer", List.of(
                                new TreeNode("2-2-1", "React Developer", List.of()),
                                new TreeNode("2-2-2", "Angular Developer", List.of())
                        )),
                        new TreeNode("2-3", "🧠 AI/ML Engineer", List.of(
                                new TreeNode("2-3-1", "NLP Engineer", List.of()),
                                new TreeNode("2-3-2", "Computer Vision Engineer", List.of())
                        )),
                        new TreeNode("2-4", "🛠️ QA & Tester", List.of(
                                new TreeNode("2-4-1", "Automation Tester", List.of()),
                                new TreeNode("2-4-2", "Manual Tester", List.of())
                        ))
                )),

                new TreeNode("3", "🗂️ Supporting Roles", List.of(
                        new TreeNode("3-1", "🧑‍💼 Project Manager", List.of()),
                        new TreeNode("3-2", "📋 Business Analyst", List.of()),
                        new TreeNode("3-3", "🎨 UI/UX Designer", List.of()),
                        new TreeNode("3-4", "🧑‍🏫 Technical Writer", List.of())
                ))
        );

        model.addAttribute("treeData", treeData);
        ViewHelper.setView(model, "view/tree", "Tree View with Details");
        return "layout";
    }
}
