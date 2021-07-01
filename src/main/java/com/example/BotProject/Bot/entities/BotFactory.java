package com.example.BotProject.Bot.entities;

import com.example.BotProject.Bot.Bot;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BotFactory implements FactoryBean<Bot> {
    @Autowired
    public VisitorRepo repo;

    private String name;
    private String token;

    public void newBot(String name, String token){
        this.name=name;
        this.token=token;
    }


    //Методы из FactoryBean
    @Override
    public Bot getObject() throws Exception {
        //Здесь создается новый бот. Данные достаем из таблицы
       return new Bot(name,token,repo);
    }

    @Override
    public Class<?> getObjectType() {
        return Bot.class;
    }
}
