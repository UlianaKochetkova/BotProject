package com.example.BotProject.AppForBot.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BotProject.AppForBot.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    //boolean exists(Users)
}
