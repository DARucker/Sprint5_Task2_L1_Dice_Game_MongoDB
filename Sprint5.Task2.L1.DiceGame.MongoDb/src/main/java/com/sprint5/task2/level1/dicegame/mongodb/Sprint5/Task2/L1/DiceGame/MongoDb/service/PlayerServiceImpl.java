package com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.service;

import com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.dto.PlayerToSave;
import com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.entity.Game;
import com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.entity.Player;
import com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.dto.Playerdto;
import com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.entity.Result;
import com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.repository.PlayerRepoMongo;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PlayerServiceImpl implements IPlayerServiceMongo {
    @Autowired
    private PlayerRepoMongo playerRepoMongo;

    private static final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Override
    public PlayerToSave create (PlayerToSave playerToSave){

        if(!playerToSave.getName().equals("")) {
            Optional<Player> playerDb = playerRepoMongo.findByName(playerToSave.getName());
            if (playerDb.isPresent()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Te player with name " + playerToSave.getName() + " exists.");
            }
        }
        Player playerForDb = new Player();
        List<Player> lista = playerRepoMongo.findAll();
        OptionalInt ultimoId = lista.stream().mapToInt(x -> Integer.valueOf(x.getId())).max();
        if(ultimoId.isPresent()){
            int ultimoIdInt = ultimoId.getAsInt();
            int nuevoId = ultimoIdInt + 1;
            String id = Integer.toString(nuevoId);
            playerForDb.setId(id);
        }else {
            playerForDb.setId("1");
        }
        playerForDb.setCreated(LocalDateTime.now());
        playerForDb.setName(playerToSave.getName());
        List<Game> gameList = new ArrayList<>();
        playerForDb.setGames(gameList);

        Player saved = playerRepoMongo.save(playerForDb);

        return entityToPlayerToSave(saved);
    }

    @Override
    public PlayerToSave update (PlayerToSave playerToSave){
            log.info("update player: " + playerToSave);
            Optional<Player> playerDb = playerRepoMongo.findById(playerToSave.getId());
            if (!playerDb.isPresent()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Te player with id " + playerToSave.getId() + " does not exists.");
            }
            Player playerUpdate = playerDb.get();
            playerUpdate.setName(playerToSave.getName());
            Player updated = playerRepoMongo.save(playerUpdate);
            return entityToPlayerToSave(updated);
        }

    /*
     * DELETE /players/{id}/games: elimina las tiradas del jugador/a.
     */

    @Override
    public void deleteGamesByPlayerId(String id){
        log.info("update player: " + id);
        Optional<Player> playerDb = playerRepoMongo.findById(id);
        if (!playerDb.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Te player with id " + id + " does not exists.");
        }
        Player playerUpdate = playerDb.get();
        List<Game> gameList = new ArrayList<>();
        playerUpdate.setGames(gameList);
        Player updated = playerRepoMongo.save(playerUpdate);
        }

        @Override
        public Playerdto findById(String id){
            log.info("Find by Id: " + id);
            Optional<Player> playerDb = playerRepoMongo.findById(id);
            if (!playerDb.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Te player with id " + id + " does not exists.");
            }
        return entityToDto(playerDb.get());
        }







    @Override
    public Playerdto playGame(String id){

        Optional<Player> playerDb = playerRepoMongo.findById(id);
        if (!playerDb.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Te player with id " + id + " does not exists.");
        }

        Player playerUpdate = playerDb.get();
        List<Game> gameList = playerUpdate.getGames();

        if(gameList.isEmpty()){
            gameList = new ArrayList<>();
        }
        int dice1 = (int) (Math.random()*6);
        int dice2 = (int) (Math.random()*6);
        int points = dice1+dice2;
        Game game = new Game();
        if(points == 7){
            game.setResult(Result.WIN);
        }else {
            game.setResult(Result.LOOSE);
        }
        game.setPoints(points);
        gameList.add(game);
        playerUpdate.setGames(gameList);
        playerRepoMongo.save(playerUpdate);
        return entityToDto(playerUpdate);

    }






    /**
     * This method transform an entity into a DTO
     * @param player
     * @return playerdto
     */
    @Override
    public Playerdto entityToDto(Player player) {
        Playerdto playerdto = modelMapper().map(player, Playerdto.class);
        if(playerdto.getName().equals("")){
            playerdto.setName("Anonymous");
        }
        return playerdto;
    }
    /**
     * This method recives a DTO object to transform it into an entity
     * @param playerdto
     * @return player
     */
    @Override
    public Player dtoToEntity(Playerdto playerdto) {
        Player player = modelMapper().map(playerdto, Player.class);
        return player;
    }

    /**
     *
     * @param player
     * @return playerToSave
     */
    public PlayerToSave entityToPlayerToSave(Player player) {
        PlayerToSave playerToSave = modelMapper().map(player, PlayerToSave.class);
        return playerToSave;
    }
}
