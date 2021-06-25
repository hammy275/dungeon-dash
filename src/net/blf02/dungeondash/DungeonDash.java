package net.blf02.dungeondash;

import net.blf02.dungeondash.commands.MainExecutor;
import net.blf02.dungeondash.game.DDMap;
import net.blf02.dungeondash.utils.Tracker;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DungeonDash extends JavaPlugin {

    public static final String VERSION = "0.1.0";
    // Ends in trailing /.
    public static String serverDir;
    // Ends in trailing /
    public static String mapsDir;

    @Override
    public void onEnable() {
        super.onEnable();
        // Master command to handle everything
        serverDir = this.getServer().getWorldContainer().getAbsolutePath();
        this.getCommand("ddash").setExecutor(new MainExecutor());
        Path mapsDirPath = Paths.get(serverDir, "dungeondash_maps");
        mapsDir = mapsDirPath.toString();
        if (!mapsDir.endsWith("/")) {
            mapsDir += "/";
        }
        File mapsDir = mapsDirPath.toFile();
        if (!mapsDir.exists()) {
            if (mapsDir.mkdirs()) {
                System.out.println("Successfully made directory for DungeonDash maps!");
            } else {
                System.out.println("Failed to make directory " + mapsDir + " for DungeonDash maps!");
                throw new RuntimeException("Could not make directory for DungeonDash maps!");
            }
        } else {
            File[] possibleMaps = mapsDir.listFiles();
            if (possibleMaps == null) {
                System.out.println("Failed to load maps!");
            } else {
                for (File file : possibleMaps) {
                    if (file.isFile()) {
                        DDMap possibleMap = DDMap.loadMap(file.getAbsolutePath());
                        if (possibleMap != null) {
                            Tracker.maps.add(possibleMap);
                            System.out.println("Loaded map " + possibleMap.mapDisplayName + "!");
                        } else {
                            System.out.println(file + " is not a valid map!");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
