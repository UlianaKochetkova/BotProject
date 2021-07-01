package com.example.BotProject.AppForBot.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface BotRepository extends JpaRepository<DataBot, Integer> {
    @Query("SELECT t FROM DataBot t WHERE t.bot_name = ?1")
    DataBot findBotByBot_name(String bot_name);

    @Query("SELECT t FROM DataBot t WHERE t.bot_name = ?1 AND t.bot_token=?2")
    DataBot findBotByBot_nameAndBot_token(String bot_name, String bot_token);

}
