package net.blf02.dungeondash.game;

import net.blf02.dungeondash.config.Config;
import net.blf02.dungeondash.config.Constants;
import net.blf02.dungeondash.utils.Tracker;
import net.blf02.dungeondash.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerState implements Comparable<PlayerState> {

    public DDMap map;
    public Player player;
    public Lobby lobby;
    public boolean inLobby = true;
    public Location lastPosition = new Location(null, 0, -5, 0);
    public int ticksStill = 0;
    public Location respawnPoint;
    public Scoreboard scoreboard = Tracker.manager.getNewScoreboard();
    public Objective objective = scoreboard.getObjective("ddash_scoreboard") == null ?
            scoreboard.registerNewObjective("ddash_scoreboard", "dummy", Constants.scoreboardTag)
            : scoreboard.getObjective("ddash_scoreboard");
    public BeforeGameState beforeGameState;

    public PlayerState(DDMap map, Player player, Lobby lobby) {
        this.map = map;
        this.player = player;
        this.lobby = lobby;
        this.respawnPoint = this.map.start;
        this.beforeGameState = new BeforeGameState(player);

        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.player.setScoreboard(scoreboard);

        player.setFallDistance(0);
        player.teleport(map.start);
        player.setFallDistance(0);
        Util.sendMessage(player,
                "You have entered the lobby! Feel free to practice, the game will begin shortly!");
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(999999);
        for (PotionEffect p : player.getActivePotionEffects()) {
            player.removePotionEffect(p.getType());
        }
    }

    public void doRespawn(Player player, boolean forceRespawn) {
        player.setHealth(20);
        player.setFireTicks(0);
        if (this.map.respawnsKill && !forceRespawn) {
            Tracker.playStatus.get(player.getDisplayName()).triggerLoss();
        } else {
            player.setFallDistance(0);
            player.teleport(this.respawnPoint);
            player.setFallDistance(0);
        }
    }

    public void attemptRespawn(Player player) {
        doRespawn(player, false);
    }

    public void leaveGame(boolean useAsync) {
        Tracker.playStatusesToRemove.add(player.getDisplayName());
        this.beforeGameState.restoreState(useAsync);
        PlayerState toRemove = null;
        for (PlayerState s : this.lobby.playerStates) {
            if (s.player.getDisplayName().equals(player.getDisplayName())) {
                toRemove = s;
                break;
            }
        }
        if (toRemove != null) {
            this.lobby.playerStates.remove(toRemove);
        }
        // Clear scoreboard
        player.setScoreboard(Tracker.manager.getNewScoreboard());
    }

    public void triggerLoss() {
        if (inLobby) {
            this.doRespawn(player, true);
        } else {
            Util.sendMessage(player, "You lose!");
            player.sendTitle(ChatColor.DARK_RED + "You lose!", null, 5, 50, 5);
            leaveGame(true);
        }
    }

    public void triggerVictory() {
        if (inLobby) {
            this.doRespawn(player, true);
        } else {
            Util.sendMessage(this.player, "You win!");
            player.sendTitle(ChatColor.GREEN + "You win!", null, 5, 50, 5);
            leaveGame(true);
        }
    }

    public void noStillCheck() {
        if (!inLobby) {
            Location newPos = player.getLocation();
            if (Math.abs(newPos.getX() - lastPosition.getX()) < Config.stillDistance &&
                    Math.abs(newPos.getY() - lastPosition.getY()) < Config.stillDistance &&
                    Math.abs(newPos.getZ() - lastPosition.getZ()) < Config.stillDistance) {
                ticksStill++;
            }
            this.lastPosition = newPos;
        }
    }

    public boolean isInEndingZone() {
        boolean xIn;
        boolean zIn;
        if (map.endCorner1.getX() < map.endCorner2.getX()) {
            xIn = player.getLocation().getX() >= map.endCorner1.getX() &&
                    player.getLocation().getX() <= map.endCorner2.getX();
        } else {
            xIn = player.getLocation().getX() >= map.endCorner2.getX() &&
                    player.getLocation().getX() <= map.endCorner1.getX();
        }
        if (map.endCorner1.getZ() < map.endCorner2.getZ()) {
            zIn = player.getLocation().getZ() >= map.endCorner1.getZ() &&
                    player.getLocation().getZ() <= map.endCorner2.getZ();
        } else {
            zIn = player.getLocation().getZ() >= map.endCorner2.getZ() &&
                    player.getLocation().getZ() <= map.endCorner1.getZ();
        }
        return xIn && zIn;
    }


    @Override
    public int compareTo(PlayerState p) {
        // This compareTo evaluates such that this.compareTo(p) < 0 is true if this is FARTHER from the finish than p
        if (p == null) {
            throw new NullPointerException("Cannot compare PlayerState to null-value!");
        } else {
            Location center = this.map.getCenterOfEnd();
            return (int) ((this.player.getLocation().distance(center) - p.player.getLocation().distance(center)) * -1000);
        }
    }
}
