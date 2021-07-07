package com.example.BotProject.Bot.entities;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitorRepo extends JpaRepository<Visitor,Integer> {
    @Query("select t from Visitor t where t.first_name=?1")
    Visitor findVisitorByFirst_name(String name);

    @Query("select t from Visitor t where t.chatIdFrom=?1")
    Visitor findVisitorByChatIdFrom(Long chatId);
}

