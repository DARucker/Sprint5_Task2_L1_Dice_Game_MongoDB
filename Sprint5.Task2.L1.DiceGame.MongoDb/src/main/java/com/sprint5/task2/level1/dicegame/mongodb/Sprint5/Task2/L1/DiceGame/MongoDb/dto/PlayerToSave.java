package com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PlayerToSave {
    @Schema(description = "This is the id of the Player. The id is autogenerated by the database")
    private String id;
    @Schema(description = "This field is the NAME of the Player. If empty, the system will show Anonymous.", example = "Dario", required = false)
    private String name;
}