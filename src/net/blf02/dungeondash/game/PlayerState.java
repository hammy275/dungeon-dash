package net.blf02.dungeondash.game;

import net.blf02.dungeondash.utils.Tracker;
import net.blf02.dungeondash.utils.Util;
import net.minecraft.server.v1_16_R3.Position;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerState {

    public DDMap map;
    public Location locationBeforePlaying;
    public Player player;
    public Lobby lobby;
    public boolean inLobby = true;
    public Position lastPosition = new Position(0, -5, 0);
    public int ticksStill = 0;

    public final double stillDistance = 0.009;

    public PlayerState(DDMap map, Player player, Lobby lobby) {
        this.map = map;
        this.locationBeforePlaying = player.getLocation();
        this.player = player;
        this.lobby = lobby;
    }

    public void leaveGame() {
        Tracker.playStatusesToRemove.add(player.getDisplayName());
        player.setFallDistance(0);
        player.teleport(this.locationBeforePlaying);
        player.setFallDistance(0);
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
    }

    public void triggerLoss() {
        if (inLobby) {
            this.map.doRespawn(player, true);
        } else {
            Util.sendMessage(player, "You lose!");
            leaveGame();
        }
    }

    public void triggerVictory() {
        if (inLobby) {
            this.map.doRespawn(player, true);
        } else {
            Util.sendMessage(this.player, "You win!");
            leaveGame();
        }
    }

    public void noStillCheck() {
        if (!inLobby) {
            Location loc = player.getLocation();
            Position newPos = new Position(loc.getX(), loc.getY(), loc.getZ());
            if (Math.abs(newPos.getX() - lastPosition.getX()) < stillDistance &&
                    Math.abs(newPos.getY() - lastPosition.getY()) < stillDistance &&
                    Math.abs(newPos.getZ() - lastPosition.getZ()) < stillDistance) {
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
}
