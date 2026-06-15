package com.example.client.web;

import com.example.client.session.UserSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Exposes the logged-in user's name and role to every view (for the nav bar).
 */
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
