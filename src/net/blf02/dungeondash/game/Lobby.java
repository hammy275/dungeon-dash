package net.blf02.dungeondash.game;

import net.blf02.dungeondash.config.Config;
import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Lobby {

    public boolean gameStarted = false;
    public List<PlayerState> playerStates = new ArrayList<>();
    public int ticksUntilStart = Config.ticksInLobby;
    public ArmorStand chaser = null;
    public Queue<PlayerState> positionsLastToFirst = new PriorityQueue<>();

    public void cleanLobby() {
        // Remove chaser if it exists
        if (chaser != null) {
            chaser.remove();
        }
        // Lose all players
        for (PlayerState p : playerStates) {
            p.triggerLoss();
        }
    }

    public void resetRankings() {
        positionsLastToFirst.clear();
        positionsLastToFirst.addAll(this.playerStates);
    }
}
