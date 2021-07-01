package com.example.BotProject.AppForBot.entities;

import javax.persistence.*;

@Entity
public class DataBot {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    private String bot_name;
    private String bot_token;

    public DataBot(String name, String token){
        this.bot_name=name;
        this.bot_token=token;
    }
    public DataBot(){

    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBot_name() {
        return bot_name;
    }

    public void setBot_name(String bot_name) {
        this.bot_name = bot_name;
    }

    public String getBot_token() {
        return bot_token;
    }

    public void setBot_token(String bot_token) {
        this.bot_token = bot_token;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==this){
            return true;
        }
        if (obj==null){
            return false;
        }
        if (this.getClass()!=obj.getClass())
            return false;
        DataBot o=(DataBot)obj;
        if (bot_name!=o.getBot_name()){
            return false;
        }
        if (bot_token!=o.getBot_token()){
            return false;
        }

        return true;
    }
}
