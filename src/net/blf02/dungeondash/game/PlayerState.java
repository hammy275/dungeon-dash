package net.blf02.dungeondash.game;

import net.minecraft.server.v1_16_R3.Position;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerState {

    public DDMap map;
    public Location locationBeforePlaying;
    public Player player;
    public boolean inLobby = false; // Lobby goes unused, so TODO: implement a lobby system
    public Position lastPosition = new Position(0, -5, 0);
    public int ticksStill = 0;

    public PlayerState(DDMap map, Player player) {
        this.map = map;
        this.locationBeforePlaying = player.getLocation();
        this.player = player;
    }

    public void noStillCheck() {
        if (!inLobby) {
            Location loc = player.getLocation();
            Position newPos = new Position(loc.getX(), loc.getY(), loc.getZ());
            if (Math.abs(newPos.getX() - lastPosition.getX()) < 0.5 &&
                    Math.abs(newPos.getY() - lastPosition.getY()) < 0.5 &&
                    Math.abs(newPos.getZ() - lastPosition.getZ()) < 0.5) {
                ticksStill++;
            }
            this.lastPosition = newPos;
        }
    }

    public boolean isInEndingZone() {
        if (!inLobby) {
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
        return false;
    }
}
