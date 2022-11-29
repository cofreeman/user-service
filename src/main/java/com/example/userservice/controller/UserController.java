package com.example.userservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    Environment environment;

    @Autowired
    public UserController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/health_check")
    public String status(){
        return "It's ok";
    }

    @GetMapping("/welcome")
    public String welcome(){
        return environment.getProperty("greeting.message");
    }
}
