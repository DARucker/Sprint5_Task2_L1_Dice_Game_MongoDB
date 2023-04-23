package com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.service;

import com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.dto.PlayerToSave;
import com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.dto.Playerdto;
import com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.entity.Player;
import org.springframework.stereotype.Service;

@Service
public interface IPlayerServiceMongo {


    PlayerToSave create (PlayerToSave playerToSave);
    PlayerToSave update (PlayerToSave playerToSave);
    void deleteGamesByPlayerId(String id);
    Playerdto findById(String id);
    Playerdto playGame(String id);
    Playerdto entityToDto(Player player);
    Player dtoToEntity(Playerdto playerdto);
}
