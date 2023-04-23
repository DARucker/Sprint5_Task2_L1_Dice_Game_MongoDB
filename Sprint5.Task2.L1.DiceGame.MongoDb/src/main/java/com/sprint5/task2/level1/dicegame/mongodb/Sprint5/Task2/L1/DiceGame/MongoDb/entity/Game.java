package com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Game {
    @Id
    private String id;
    private int points;
    //private String resultGame;
    private Result result;

}
