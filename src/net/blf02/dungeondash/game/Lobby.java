package net.blf02.dungeondash.game;

import net.blf02.dungeondash.config.Config;
import org.bukkit.entity.EnderDragon;

import java.util.ArrayList;
import java.util.List;

public class Lobby {

    public boolean gameStarted = false;
    public List<PlayerState> playerStates = new ArrayList<>();
    public int ticksUntilStart = Config.ticksInLobby;
    public EnderDragon chaser = null;

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
}
