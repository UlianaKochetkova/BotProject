package com.example.BotProject.Bot.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Wait {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer visitor_id;
    private Long chatIdFrom;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVisitor_id() {
        return visitor_id;
    }

    public void setVisitor_id(Integer visitor_id) {
        this.visitor_id = visitor_id;
    }

    public Long getChatIdFrom() {
        return chatIdFrom;
    }

    public void setChatIdFrom(Long chatIdFrom) {
        this.chatIdFrom = chatIdFrom;
    }


}
