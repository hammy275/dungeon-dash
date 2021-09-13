package net.blf02.dungeondash.utils;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Although provided by the base game, this class isn't provided by Bukkit/Spigot.
 *
 * We use our own for convenience’s sake.
 */
public class Position {

    protected final double x;
    protected final double y;
    protected final double z;

    public Position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position(Location loc) {
        this(loc.getX(), loc.getY(), loc.getZ());
    }

    public Location asLocation(World world) {
        return new Location(world, x, y, z, 0, 0);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }
}
