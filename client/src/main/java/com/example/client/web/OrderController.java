package com.example.client.web;

import com.example.client.model.LineItemView;
import com.example.client.model.OrderView;
import com.example.client.service.BackendClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final BackendClient backend;

    public OrderController(BackendClient backend) {
        this.backend = backend;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", backend.listOrders());
        return "orders";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        OrderView order = new OrderView();
        order.getLineItems().add(new LineItemView());
        model.addAttribute("order", order);
        model.addAttribute("mode", "create");
        return "order-form";
    }

    @PostMapping
    public String create(@ModelAttribute OrderView order) {
        backend.createOrder(order);
        return "redirect:/orders";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        OrderView order = backend.getOrder(id);
        if (order.getLineItems().isEmpty()) {
            order.getLineItems().add(new LineItemView());
        }
        model.addAttribute("order", order);
        model.addAttribute("mode", "edit");
        return "order-form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute OrderView order) {
        backend.updateOrder(id, order);
        return "redirect:/orders";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        backend.deleteOrder(id);
        return "redirect:/orders";
    }
}
