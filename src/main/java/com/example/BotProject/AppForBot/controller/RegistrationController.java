package com.example.BotProject.AppForBot.controller;

import com.example.BotProject.AppForBot.entities.Role;
import com.example.BotProject.AppForBot.entities.User;
import com.example.BotProject.AppForBot.entities.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
public class RegistrationController {
    @Autowired
    UserRepository usersRepo;

    @GetMapping("/registration")
    public String registration(String msg, Model model){
        model.addAttribute("message",msg);
        return "registration";
    }


    @PostMapping("/registration")
    public String addUser(User user, Model model){
        System.out.println("username: "+user.getUsername()+"; password: "+user.getPassword());
        User found=usersRepo.findByUsername(user.getUsername());
        if (found!=null){
            return registration("User exists",model);
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        usersRepo.save(user);
        return "redirect:/login";
    }
}
