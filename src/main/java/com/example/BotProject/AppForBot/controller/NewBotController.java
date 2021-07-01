package com.example.BotProject.AppForBot.controller;

import com.example.BotProject.AppForBot.entities.DataBot;
import com.example.BotProject.AppForBot.entities.BotRepository;
import com.example.BotProject.Bot.Bot;
import com.example.BotProject.Bot.entities.BotFactory;
import com.example.BotProject.Bot.entities.VisitorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@Controller
public class NewBotController {
    @Autowired
    BotRepository botrepo;

    @Autowired
    VisitorRepo visitorRepo;

    @Autowired
    BotFactory bf;


    @GetMapping("/newbot")
    public String newbot(String msg, Model model){
        return "newbot";
    }

    @PostMapping("/newbot")
    public String addBot(DataBot bot, Model model) throws Exception {
        if (botrepo.findBotByBot_nameAndBot_token(bot.getBot_name(),bot.getBot_token())!=null){
            model.addAttribute("message","Bot is already existing");
        }
        else {
            botrepo.save(bot);
            //проверка на корректность - "Ваш бот загружен успешно"
            if (botrepo.findBotByBot_nameAndBot_token(bot.getBot_name(), bot.getBot_token())!=null) {
                //создаем бот
                //409е были вызваны тем, что если бот уже существует, то вызывать new нельзя, насколько я понимаю
                bf.newBot(bot.getBot_name(),bot.getBot_token());
                Bot b=bf.getObject();
                TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
                telegramBotsApi.registerBot(b);

                model.addAttribute("message","Successful creating");
            }

        }
        return "newbot";
    }


}
