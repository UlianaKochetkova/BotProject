package com.example.BotProject.AppForBot.controller;

import com.example.BotProject.AppForBot.entities.DataBot;
import com.example.BotProject.AppForBot.entities.BotRepository;
import com.example.BotProject.Bot.Bot;
import com.example.BotProject.Bot.entities.BotFactory;
import com.example.BotProject.Bot.entities.VisitorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
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

    /**
     * Обработка PUT-запроса
     * Случаи:
     * 1) Бот существует
     * 2) Бот найден по имени. Токен меняется
     * 3) Бот найден по токену. Имя меняется
     * 4) Создание полностью нового бота
     * @param name
     * @param token
     * @param model
     * @return
     * @throws Exception
     */
    @PutMapping("/newbot")
    public String addBot(@RequestParam(required = false,name="bot_name") String name, @RequestParam(required = false, name="bot_token") String token, Model model) throws Exception {
         if (botrepo.findBotByBot_nameAndBot_token(name,token)!=null){
            model.addAttribute("message","Bot is already existing");
        }
        else if (botrepo.findBotByBot_name(name)!=null){
            DataBot b=botrepo.findBotByBot_name(name);
            b.setBot_token(token);
            botrepo.save(b);
            model.addAttribute("message","This bot was found by name. Token was modified");
             //TODO: нужно ли перезапускать бота при переименовании/при новом токене?
        }
        else if (botrepo.findDataBotByBot_token(token)!=null){
            DataBot b=botrepo.findDataBotByBot_token(token);
            b.setBot_name(name);
            botrepo.save(b);
            model.addAttribute("message","This bot was found by token. Name was modified");
            //TODO: нужно ли перезапускать бота при переименовании/при новом токене?
        }
        else {
            DataBot b=new DataBot(name,token);
            botrepo.save(b);

//            bf.newBot(b.getBot_name(),b.getBot_token());
//            Bot bot=bf.getObject();
//            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
//            telegramBotsApi.registerBot(bot);

            model.addAttribute("message","Successful creating");
        }

        return "newbot";
    }

}
