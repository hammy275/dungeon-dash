package net.blf02.dungeondash.game;

import net.blf02.dungeondash.DungeonDash;
import net.blf02.dungeondash.utils.Tracker;
import net.blf02.dungeondash.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

    public boolean respawnsKill = false;
    public boolean voidRespawns = false;
    public boolean waterRespawns = false;
    public boolean hasChaser = true;
    public String iconId = "COMPASS";
    public int mapVersion = 2;

    public transient boolean isFullMap;

    public DDMap(String mapDisplayName, Location start, Location end1, Location end2) {
        this.mapDisplayName = mapDisplayName;
        this.start = start;
        this.endCorner1 = end1;
        this.endCorner2 = end2;
        this.isFullMap = mapDisplayName != null && start != null &&
                end1 != null && end2 != null;
    }

    public Location getCenterOfEnd() {
        return new Location(this.endCorner1.getWorld(),
                (Math.abs(this.endCorner1.getX()) + Math.abs(this.endCorner2.getX())) / 2.0,
                (Math.abs(this.endCorner1.getY()) + Math.abs(this.endCorner2.getY())) / 2.0,
                (Math.abs(this.endCorner1.getZ()) + Math.abs(this.endCorner2.getZ())) / 2.0,
                0, 0);
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
            Util.sendMessage(sender, "Failed to save map data! Maybe try setting the second corner again?");
            return false;
        }
    }

    public boolean saveMap(@Nullable CommandSender sender, DDMap oldMap) {
        boolean res = saveMap(sender);
        Tracker.maps.remove(oldMap);
        return res;
    }

    public ItemStack getGUIIcon() {
        Material itemMat = Material.getMaterial(iconId);
        if (itemMat == null) itemMat = Material.COMPASS;
        ItemStack toRet = new ItemStack(itemMat);
        ItemMeta meta = toRet.getItemMeta();
        ChatColor color = this.hasChaser ? ChatColor.RED : ChatColor.BLUE;
        meta.setDisplayName(ChatColor.RESET + color.toString() + this.mapDisplayName);
        toRet.setItemMeta(meta);
        return toRet;
    }

    public static DDMap loadMap(String path) {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(
                    new GZIPInputStream(new FileInputStream(path)));
            DDMap map = (DDMap) in.readObject();
            map.hasChaser = true;
            in.close();
            return map;
        } catch (ClassNotFoundException | IOException e) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        // No two maps share the same name, so we're safe to use the hash from the map name.
        return this.mapDisplayName.hashCode();
    }
}
