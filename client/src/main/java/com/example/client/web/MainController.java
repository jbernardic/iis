package com.example.client.web;

import com.example.client.service.BackendClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final BackendClient backend;

    public MainController(BackendClient backend) {
        this.backend = backend;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        try {
            model.addAttribute("activeSource", backend.activeSource());
        } catch (Exception e) {
            model.addAttribute("activeSource", "(backend unavailable)");
        }
        return "dashboard";
    }
}
