package com.example.BotProject;

import com.example.BotProject.Bot.entities.BotFactory;
import com.example.BotProject.Bot.entities.Visitor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        //BotFactory bf=new BotFactory("practice_first_bot","1892319457:AAHyS_c360wsMUJv_wG6WA9MxjqykfXZ2TY");
        SpringApplication.run(Application.class, args);
    }
}
