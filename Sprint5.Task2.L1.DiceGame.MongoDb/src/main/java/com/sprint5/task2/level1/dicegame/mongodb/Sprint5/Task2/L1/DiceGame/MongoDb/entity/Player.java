package com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;
import java.util.List;

@Document(value = "player")
@Data
public class Player {

    @Id
    private String id;
    private String name;
    private LocalDateTime created;

    private List<Game> games;

}
