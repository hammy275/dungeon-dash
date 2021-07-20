package net.blf02.dungeondash.event;

import net.blf02.dungeondash.config.Config;
import net.blf02.dungeondash.game.CreateState;
import net.blf02.dungeondash.game.DDMap;
import net.blf02.dungeondash.game.Lobby;
import net.blf02.dungeondash.game.PlayerState;
import net.blf02.dungeondash.utils.Tracker;
import net.blf02.dungeondash.utils.Util;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;

import java.util.Map;

public class ConstantTick {

    public static final Particle.DustOptions endBorderColor = new Particle.DustOptions(Color.RED, 1);

    public static void handleEveryTick() {
        handlePlayersPlaying();
        tickLobbies();
        tickLobbyChasers();
        handleCreateStates();
    }

    public static void handlePlayersPlaying() {
        for (Map.Entry<String, PlayerState> entry : Tracker.playStatus.entrySet()) {
            entry.getValue().noStillCheck();
            if (entry.getValue().isInEndingZone()) {
                // Win!
                entry.getValue().triggerVictory();
                // If someone wins, and there's not a chaser, lose everyone else.
                Lobby lobby = Tracker.lobbies.get(entry.getValue().map);
                if (lobby != null && !entry.getValue().map.hasChaser) {
                    lobby.cleanLobby();
                }
            } else if (entry.getValue().ticksStill >= Config.ticksStillUntilDeath) {
                entry.getValue().triggerLoss();
                entry.getValue().ticksStill = 0;
            } else if (entry.getValue().player.getLocation().getY() < 0 && entry.getValue().map.voidRespawns) {
                entry.getValue().attemptRespawn(entry.getValue().player);
            } else if (entry.getValue().player.getLocation().getBlock().getType() == Material.WATER
                    && entry.getValue().map.waterRespawns) {
                entry.getValue().attemptRespawn(entry.getValue().player);
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
            if (lobby.ticksUntilStart >= -101 && !lobby.playerStates.isEmpty()) {
                lobby.ticksUntilStart--;
            }
            if (lobby.playerStates.isEmpty()) {
                lobby.cleanLobby();
                Tracker.lobbiesToRemove.add(entry.getKey());
                continue;
            }
            if (lobby.ticksUntilStart > 0 && lobby.ticksUntilStart < 61) {
                for (PlayerState p : lobby.playerStates) {
                    p.player.setFallDistance(0);
                    p.player.teleport(entry.getKey().start);
                    p.player.setFallDistance(0);
                    p.player.setHealth(20);
                }
            }
            if (lobby.ticksUntilStart == 0 && !lobby.gameStarted) {
                lobby.gameStarted = true;
                for (PlayerState p : lobby.playerStates) {
                    p.inLobby = false;
                    Util.sendMessage(p.player, "Let the games begin!");
                    p.player.sendTitle(ChatColor.GREEN + "Go!", null, 5, 20, 5);
                }
            } else if (lobby.ticksUntilStart % 20 == 0 && !lobby.gameStarted) {
                int secsUntilStart = lobby.ticksUntilStart / 20;
                if (secsUntilStart <= 5 || secsUntilStart == 10 || secsUntilStart == 20) {
                    for (PlayerState p : lobby.playerStates) {
                        Util.sendMessage(p.player, secsUntilStart + " seconds until the game begins!");
                    }
                }
                if (secsUntilStart <= 5) {
                    for (PlayerState p : lobby.playerStates) {
                        String colorPrefix = secsUntilStart == 3 ? String.valueOf(ChatColor.GREEN)
                                : (secsUntilStart == 2 ? String.valueOf(ChatColor.YELLOW)
                                : (secsUntilStart == 1 ? String.valueOf(ChatColor.RED)
                                : String.valueOf(ChatColor.DARK_GREEN)));
                        p.player.sendTitle(colorPrefix + secsUntilStart, null, 1, 60, 1);
                    }
                }
            } else if (lobby.ticksUntilStart == -100 && lobby.gameStarted && entry.getKey().hasChaser) {
                lobby.chaser = Util.spawnChaser(entry.getKey().start);
                for (PlayerState p : lobby.playerStates) {
                    Util.sendMessage(p.player, "The chaser has entered the map! Don't get caught in the smoke!");
                }
            }
        }
        for (DDMap map : Tracker.lobbiesToRemove) {
            Tracker.lobbies.remove(map);
        }
        Tracker.lobbiesToRemove.clear();
    }

    public static void tickLobbyChasers() {
        for (Map.Entry<DDMap, Lobby> entry : Tracker.lobbies.entrySet()) {
            ArmorStand chaser = entry.getValue().chaser;
            if (chaser == null) return;
            if (chaser.getTicksLived() % Config.ticksBetweenMove != 0) return;
            if (chaser.getTicksLived() % 5 == 0)
                chaser.getWorld().spawnParticle(Particle.SMOKE_NORMAL,
                        chaser.getLocation(), 32, 0.5, 0.5, 0.5, 0.01);
            PlayerState target = entry.getValue().positionsLastToFirst.peek();
            if (target != null) {
                Location destination = chaser.getLocation().setDirection(
                        target.player.getLocation().subtract(chaser.getLocation()).toVector());
                destination = destination.add(Config.distanceToMove * destination.getDirection().getX(),
                        Config.distanceToMove * destination.getDirection().getY(),
                        Config.distanceToMove * destination.getDirection().getZ());
                chaser.teleport(destination);
                if (chaser.getLocation().distance(target.player.getLocation()) <= 2) {
                    target.triggerLoss();
                    entry.getValue().resetRankings(); // Prevents targeting dead players
                }
            }
        }
    }

    public static void handleCreateStates() {
        for (Map.Entry<String, CreateState> entry : Tracker.creationStatus.entrySet()) {
            CreateState state = entry.getValue();
            if (state.map.start != null && state.map.start.distance(state.player.getLocation()) < 128) {
                state.player.spawnParticle(Particle.REDSTONE, state.map.start.getX(),
                        state.map.start.getY() + 1, state.map.start.getZ(), 1, 0.1, 0.4, 0.1,
                        0.01, new Particle.DustOptions(Color.GREEN, 1));
            }
            if (state.map.endCorner1 != null && state.map.endCorner2 != null) {
                double xCenter = (state.map.endCorner1.getX() + state.map.endCorner2.getX()) / 2;
                double xDist = Math.abs(Math.abs(state.map.endCorner1.getX()) - Math.abs(state.map.endCorner2.getX()));
                double yCenter = (state.map.endCorner1.getY() + state.map.endCorner2.getY()) / 2;
                double zCenter = (state.map.endCorner1.getZ() + state.map.endCorner2.getZ()) / 2;
                double zDist = Math.abs(Math.abs(state.map.endCorner1.getZ()) - Math.abs(state.map.endCorner2.getZ()));

                state.player.spawnParticle(Particle.REDSTONE, xCenter, yCenter, state.map.endCorner1.getZ(),
                        (int) xDist + 1, xDist / 8, 0, 0, endBorderColor);
                state.player.spawnParticle(Particle.REDSTONE, xCenter, yCenter, state.map.endCorner2.getZ(),
                        (int) xDist + 1, xDist / 8, 0, 0, endBorderColor);
                state.player.spawnParticle(Particle.REDSTONE, state.map.endCorner1.getX(), yCenter, zCenter,
                        (int) zDist + 1, 0, 0, zDist / 8, endBorderColor);
                state.player.spawnParticle(Particle.REDSTONE, state.map.endCorner2.getX(), yCenter, zCenter,
                        (int) zDist + 1, 0, 0, zDist / 8, endBorderColor);
            }
        }
    }
}
