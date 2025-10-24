package com.example.spring_boot.controllers.pages;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String products() {
        return "admin/products/products";
    }

    @GetMapping("/products/create")
    public String productsCreate() {
        return "admin/products/products-create";
    }

    @GetMapping("/products/edit/{id}")
    public String productsEdit(@PathVariable String id, org.springframework.ui.Model model) {
        model.addAttribute("productId", id);
        return "admin/products/products-edit";
    }

    @GetMapping("/customers")
    public String customers() {
        return "admin/customers";
    }

    // Categories Management
    @GetMapping("/categories")
    public String categories() {
        return "admin/products/categories";
    }

    @GetMapping("/users")
    public String User() {
        return "admin/users";
    }

    @GetMapping("/upload-demo")
    public String uploadDemo() {
        return "admin/upload-demo";
    }

}
