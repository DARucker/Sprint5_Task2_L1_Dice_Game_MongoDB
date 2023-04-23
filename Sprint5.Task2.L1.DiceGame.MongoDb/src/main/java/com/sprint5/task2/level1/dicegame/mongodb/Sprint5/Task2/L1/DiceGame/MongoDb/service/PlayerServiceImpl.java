package com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.service;

import com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.dto.PlayerToSave;
import com.sprint5.task2.level1.dicegame.mongodb.Sprint5.Task2.L1.DiceGame.MongoDb.dto.Ranking;
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
import java.util.stream.Collectors;


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

    /**
     * **  GET /players/: devuelve el listado de todos los jugadores/as
     * del sistema con su porcentaje medio de éxitos.
     */

    @Override
    public List<Ranking> listAllRanking(){
        List<Ranking> allRanking = new ArrayList<>();
        List<Player> playerList = playerRepoMongo.findAll();
        for (Player player : playerList){
            String playerId = player.getId();
            String name = player.getName();
            if(name.equals("")){
                name="Anonymous";
            }
            List<Game> gamesPlayer = player.getGames();
            int win = (int) gamesPlayer.stream().filter(x -> x.getResult().equals(Result.WIN)).count();
            int played = gamesPlayer.size();
            double calcularRatio = 0;
            if(win>0){calcularRatio =  (double) win /played*100;}
            int ratio = (int) calcularRatio;
            Ranking ranking = new Ranking(playerId, name, win, played, ratio);
            allRanking.add(ranking);
            log.info("ranking "+ ranking);
        }
        return allRanking;
    }

    /**
     * GET /players/ranking: devuelve el ranking medio de todos los jugadores/as del sistema. Es decir, el porcentaje medio de logros.
     */
    @Override
    public int rankingAvg(){
        List<Ranking> rankings = listAllRanking();
        int gamesWon = rankings.stream().mapToInt(x -> x.getWin()).sum();
        int gamesPlayed  = rankings.stream().mapToInt(x -> x.getPlayed()).sum();
        double calcularRatio = 0;
        if(gamesWon>0){
            calcularRatio =  (double) gamesWon /gamesPlayed *100;
        }
        return (int) calcularRatio;
    }

    /**
     *     GET /players/ranking/loser: devuelve al jugador/a con peor porcentaje de éxito.
     */
    @Override
    public Ranking worstPlayer(){
        List<Ranking> worstRankings = listAllRanking().stream()
                .filter(x -> x.getPlayed()>0)
                .sorted(Comparator.comparingInt(Ranking::getRatio))
                .limit(1)
                .collect(Collectors.toList());
        if(worstRankings.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No games were stored");
        }
        return worstRankings.get(0);
    }

    /**
     *     GET /players/ranking/winer: devuelve al jugador/a con mejor porcentaje de éxito.
     */
    @Override
    public Ranking bestPlayer(){
        List<Ranking> bestRankings = listAllRanking()
                .stream()
                .sorted(Comparator.comparingInt(Ranking::getRatio).reversed())
                .limit(1)
                .collect(Collectors.toList());
        if(bestRankings.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No games were stored");
        }
        return bestRankings.get(0);
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
