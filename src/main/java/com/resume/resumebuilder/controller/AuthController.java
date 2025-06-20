//package com.resume.resumebuilder.controller;
//
//import com.resume.resumebuilder.entity.User;
//import com.resume.resumebuilder.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//@Controller
//public class AuthController {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @GetMapping("/register")
//    public String showRegisterForm() {
//        return "register";  // register.html
//    }
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @PostMapping("/register")
//    public String registerUser(@RequestParam String username, @RequestParam String password) {
//        User user = new User();
//        user.setUsername(username);
//        user.setPassword(passwordEncoder.encode(password)); // encrypt password
//        userRepository.save(user);
//        return "redirect:/login";
//    }
//
//    @GetMapping("/Authhome")
//    public String showAuthHome() {
//        return "home";
//    }
//    @GetMapping("/login")
//    public String showLoginForm() {
//        return "login";
//    }
//
//
//    
//}
