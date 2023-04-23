package com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.controller;

import com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.dto.PlayerToSave;
import com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.dto.Playerdto;
import com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.service.IPlayerServiceMongo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/player")
@Tag(name = "Spring 5 - Task 2 - Dice Game", description = "")
@RestController
public class PlayerController {

    private static Logger log = LoggerFactory.getLogger(PlayerController.class);

    @Autowired
    private IPlayerServiceMongo playerServiceMongo;

    /**
     * This method creates a Player
     * @param playerToSave
     * @return ResponseEntity<Playerdto>
     */
    @PostMapping(value="/add")
    @Operation(summary= "" +
            "Adds a new Player", description = "Creates a new player and saves it in the database")
    @ApiResponse(responseCode = "200", description = "Player created correctly", content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = PlayerToSave.class))})
    @ApiResponse(responseCode = "403", description = "The player already exists", content = @Content)
    public ResponseEntity<?> createPlayer(@RequestBody PlayerToSave playerToSave){
        log.info("create player: " + playerToSave);
        try {
            playerServiceMongo.create(playerToSave);
            return ResponseEntity.ok(playerToSave);
        }catch (ResponseStatusException e){
            return new ResponseEntity<Map<String,Object>>(this.message(e), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * PUT /players: Updates the name of an existing player.
     */
    @Operation(summary= "Update Player", description = "Updates the name of an existing player")
    @ApiResponse(responseCode = "201", description = "Player updated correctly", content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = PlayerToSave.class))})
    @ApiResponse(responseCode = "404", description = "Player not found", content = @Content)
    @PutMapping(value="/update/")
    public ResponseEntity<?> updatePlayer(@RequestBody PlayerToSave playerToSave){
        log.info("update player: " + playerToSave);
        try {

            playerServiceMongo.update(playerToSave);
            return ResponseEntity.ok(playerToSave);
        }catch (ResponseStatusException e){
            return new ResponseEntity<Map<String,Object>>(this.message(e), HttpStatus.NOT_FOUND);
        }
    }
    /**
     * DELETE /players/{id}/games: deletes the rolls of a selected player.
     */
    @Operation(summary= "Delete selected games", description = "deletes all games of selected player.")
    @ApiResponse(responseCode = "200", description = "Games deleted", content = @Content)
    @ApiResponse(responseCode = "404", description = "Player not found", content = @Content)
    @DeleteMapping("/{id}/games/")
    public ResponseEntity<?> deleteGamesByPlayerId(@PathVariable String id){
        try {
            playerServiceMongo.deleteGamesByPlayerId(id);
            return ResponseEntity.ok(HttpStatus.OK);
        }catch(ResponseStatusException e){
            return new ResponseEntity<Map<String,Object>>(this.message(e), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * GET /players/{id}/games: devuelve el listado de jugadas por un jugador/a.
     * @param id
     * @return
     */
    @Operation(summary= "List all dice rolls for a player", description = "Returns the complete list of each player and the result of their dice rolls.")
    @ApiResponse(responseCode = "200", description = "List of rolls", content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = Playerdto.class))})
    @ApiResponse(responseCode = "404", description = "Player not found", content = @Content)
    @GetMapping("/{id}/games/")
    public ResponseEntity<?> findAllGames(@PathVariable String id) {
        try {
            Playerdto playerdto = playerServiceMongo.findById(id);
            return ResponseEntity.ok(playerdto);
        } catch (ResponseStatusException e) {
            Map<String, Object> error = new HashMap<>();
            return new ResponseEntity<Map<String,Object>>(this.message(e), HttpStatus.NOT_FOUND);
        }
    }

    //     public ResponseEntity<?> findAllRanking()
    //     public ResponseEntity<?> worstPlayer(){
    //     public ResponseEntity<?> bestPlayer(){


    /**
     * GAMES
     * POST /players/{id}/games/ : un jugador/a específico realiza un tirón de los dados.
     */
    @Operation(summary= "Roll dices", description = "If dice 1 + dice 2 = 7, then the player wins. The result is saved in the database")
    @ApiResponse(responseCode = "200", description = "Game added to the database", content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = Playerdto.class))})
    @ApiResponse(responseCode = "404", description = "Player not found", content = @Content)
    @PostMapping("/{id}/games/")
    public ResponseEntity<?> rollDice(@PathVariable String id){

        try {
            Playerdto playerdto = playerServiceMongo.playGame(id);
            return ResponseEntity.ok(playerdto);
        } catch (ResponseStatusException e){
            return new ResponseEntity<Map<String,Object>>(this.message(e), HttpStatus.NOT_FOUND);
        }
    }
    private Map<String, Object> message(ResponseStatusException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("Message", e.getMessage());
        error.put("Reason", e.getReason());
        return error;
    }
}
