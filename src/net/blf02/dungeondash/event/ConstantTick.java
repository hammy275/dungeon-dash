package net.blf02.dungeondash.event;

import net.blf02.dungeondash.config.Config;
import net.blf02.dungeondash.game.DDMap;
import net.blf02.dungeondash.game.Lobby;
import net.blf02.dungeondash.game.PlayerState;
import net.blf02.dungeondash.utils.Tracker;
import net.blf02.dungeondash.utils.Util;
import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;

import java.util.Map;

public class ConstantTick {

    public static void handleEveryTick() {
        handlePlayersPlaying();
        tickLobbies();
    }

    public static void handlePlayersPlaying() {
        for (Map.Entry<String, PlayerState> entry : Tracker.playStatus.entrySet()) {
            entry.getValue().noStillCheck();
            if (entry.getValue().isInEndingZone()) {
                // Win!
                entry.getValue().triggerVictory();
                // If someone wins, and there's a chaser, lose everyone else.
                Lobby lobby = Tracker.lobbies.get(entry.getValue().map);
                if (lobby != null && entry.getValue().map.hasChaser) {
                    lobby.cleanLobby();
                }
            } else if (entry.getValue().ticksStill >= Config.ticksStillUntilDeath) {
                entry.getValue().triggerLoss();
                entry.getValue().ticksStill = 0;
            } else if (entry.getValue().player.getLocation().getY() < 0 && entry.getValue().map.voidRespawns) {
                entry.getValue().map.attemptRespawn(entry.getValue().player);
            } else if (entry.getValue().player.getLocation().getBlock().getType() == Material.WATER
                    && entry.getValue().map.waterRespawns) {
                entry.getValue().map.attemptRespawn(entry.getValue().player);
            }
        }
        for (String s : Tracker.playStatusesToRemove) {
            Tracker.playStatus.remove(s);
        }
        Tracker.playStatusesToRemove.clear();
    }

    public static void tickLobbies() {
        for (Map.Entry<DDMap, Lobby> entry : Tracker.lobbies.entrySet()) {
            Lobby lobby = entry.getValue();
            if (lobby.ticksUntilStart >= -201 && !lobby.playerStates.isEmpty()) {
                lobby.ticksUntilStart--;
            }
            if (lobby.playerStates.isEmpty()) {
                lobby.cleanLobby();
                Tracker.lobbiesToRemove.add(entry.getKey());
                continue;
            }
            if (lobby.ticksUntilStart == 0 && !lobby.gameStarted) {
                lobby.gameStarted = true;
                for (PlayerState p : lobby.playerStates) {
                    p.player.setFallDistance(0);
                    p.player.teleport(entry.getKey().start);
                    p.player.setFallDistance(0);
                    p.inLobby = false;
                }
            } else if (lobby.ticksUntilStart % 20 == 0 && !lobby.gameStarted) {
                int secsUntilStart = lobby.ticksUntilStart / 20;
                if (secsUntilStart <= 5 || secsUntilStart == 10 || secsUntilStart == 20) {
                    for (PlayerState p : lobby.playerStates) {
                        Util.sendMessage(p.player, secsUntilStart + " seconds until the game begins!");
                    }
                }
            } else if (lobby.ticksUntilStart == -200 && lobby.gameStarted && entry.getKey().hasChaser) {
                lobby.chaser = Util.spawnChaser(entry.getKey().start);
            }

            // Ender Dragon AI
            if (lobby.chaser != null) {
                lobby.chaser.setPhase(EnderDragon.Phase.CHARGE_PLAYER);

            }
        }
        for (DDMap map : Tracker.lobbiesToRemove) {
            Tracker.lobbies.remove(map);
        }
        Tracker.lobbiesToRemove.clear();
    }
}
