package com.example.client.web;

import com.example.client.session.UserSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final UserSession session;

    public GlobalModelAdvice(UserSession session) {
        this.session = session;
    }

    @ModelAttribute("currentUser")
    public String currentUser() {
        return session.getUsername();
    }

    @ModelAttribute("currentRole")
    public String currentRole() {
        return session.getRole();
    }
}
