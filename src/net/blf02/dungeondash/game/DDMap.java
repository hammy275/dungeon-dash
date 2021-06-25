package net.blf02.dungeondash.game;

import net.blf02.dungeondash.DungeonDash;
import net.blf02.dungeondash.commands.MainExecutor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DDMap implements Serializable {

    private static final transient long serialVersionUID = 1791518827905011055L;

    public final String mapDisplayName;
    public final Location start;
    public final Location endCorner1;
    public final Location endCorner2;

    public boolean isFullMap;

    public DDMap(String mapDisplayName, Location start, Location end1, Location end2) {
        this.mapDisplayName = mapDisplayName;
        this.start = start;
        this.endCorner1 = end1;
        this.endCorner2 = end2;
        this.isFullMap = mapDisplayName != null && start != null &&
                end1 != null && end2 != null;
    }

    public boolean saveMap(@Nullable CommandSender sender) {
        String filePath = DungeonDash.mapsDir + this.mapDisplayName + ".data";
        try {
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(
                    new GZIPOutputStream(new FileOutputStream(filePath)));
            out.writeObject(this);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            MainExecutor.sendMessage(sender, "Failed to save map data! Maybe try setting the second corner again?");
            return false;
        }
    }

    public static DDMap loadMap(String path) {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(
                    new GZIPInputStream(new FileInputStream(path)));
            DDMap map = (DDMap) in.readObject();
            in.close();
            return map;
        } catch (ClassNotFoundException | IOException e) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        // No two maps share the same hash code, so we're safe to use the hash from the map name.
        return this.mapDisplayName.hashCode();
    }
}