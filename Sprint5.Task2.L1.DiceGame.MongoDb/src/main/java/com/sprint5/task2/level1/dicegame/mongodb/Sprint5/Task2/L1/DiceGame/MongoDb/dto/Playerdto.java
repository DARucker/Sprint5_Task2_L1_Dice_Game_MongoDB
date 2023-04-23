package com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.dto;

import com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.entity.Game;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.List;


@Data
public class Playerdto {

    @Schema(description = "This is the id of the Player. The id is autogenerated by the database")
    private String id;
    @Schema(description = "This field is the NAME of the Player. If empty, the system will show Anonymous.", example = "Dario", required = false)
    private String name;
    @Schema(description = "This field is the date of registration. The date is autogenerated by the system.", example = "31/01/2023", required = false)
    private ZonedDateTime created;
    @Schema(description = "This is the list of games played by the player and is provided by the system.", example = "", required = false)
    private List<Game> games;

}
