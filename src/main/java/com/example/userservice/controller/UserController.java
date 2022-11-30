package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-service")
@Slf4j
public class UserController {
    Environment environment;
    UserService userService;

    @Autowired
    Greeting greeting;

    @Autowired
    public UserController(Environment environment,UserService userService) {
        this.environment = environment;
        this.userService = userService;
    }

    @GetMapping("/health_check")
    public String status(){
        return String.format("It's ok Port %s", environment.getProperty("local.server.port"));
    }

    @GetMapping("/welcome")
    public String welcome(){
//        return environment.getProperty("greeting.message");
        return greeting.getMessage();
    }

    @PostMapping
    public ResponseEntity createUser(@RequestBody RequestUser user){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = modelMapper.map(user, UserDto.class);
        userService.createUser(userDto);
        ResponseUser responseUser = modelMapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseUser);
    }
}
