package net.blf02.dungeondash;

import net.blf02.dungeondash.commands.MainExecutor;
import net.blf02.dungeondash.event.ConstantSecond;
import net.blf02.dungeondash.event.ConstantTick;
import net.blf02.dungeondash.event.EventHandler;
import net.blf02.dungeondash.game.CreateState;
import net.blf02.dungeondash.game.DDMap;
import net.blf02.dungeondash.game.PlayerState;
import net.blf02.dungeondash.utils.Config;
import net.blf02.dungeondash.utils.Tracker;
import net.blf02.dungeondash.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class DungeonDash extends JavaPlugin {

    // Ends in trailing /.
    public static String serverDir;
    // Ends in trailing /
    public static String mapsDir;
    public static String playerDataDir;

    public static DungeonDash instance;

    @Override
    public void onEnable() {
        super.onEnable();

        Config.config = this.getConfig();
        Config.addDefaultOptions();
        Config.config.options().copyDefaults(true);
        this.saveConfig();
        Config.readOptions();


        instance = JavaPlugin.getPlugin(DungeonDash.class);

        // Setup scoreboards and such
        Tracker.manager = Bukkit.getScoreboardManager();

        // Set serverDir and mapsDir
        serverDir = this.getServer().getWorldContainer().getAbsolutePath();
        this.getCommand("ddash").setExecutor(new MainExecutor());
        Path mapsDirPath = Paths.get(serverDir, "dungeondash_data", "dungeondash_maps");
        Path playerDataDirPath = Paths.get(serverDir, "dungeondash_data", "playerdata");
        mapsDir = mapsDirPath.toString();
        playerDataDir = playerDataDirPath.toString();
        if (!mapsDir.endsWith("/")) {
            mapsDir += "/";
        }
        if (!playerDataDir.endsWith("/")) {
            playerDataDir += "/";
        }
        // Load all of the maps for DungeonDash
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

        File playerDataDir = playerDataDirPath.toFile();
        if (!playerDataDir.exists()) {
            if (!playerDataDir.mkdirs()) {
                System.out.println("Failed to make directory " + playerDataDir + " for DungeonDash player data!");
                throw new RuntimeException("Could not make directory for DungeonDash player data!");
            }
        }

        // Function to run every tick
        this.getServer().getScheduler().runTaskTimer(this, ConstantTick::handleEveryTick, 0, 1);
        this.getServer().getScheduler().runTaskTimer(this, ConstantSecond::handleEverySecond, 0, 20);
        this.getServer().getPluginManager().registerEvents(new EventHandler(), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (Map.Entry<String, PlayerState> entry : Tracker.playStatus.entrySet()) {
            Util.sendMessage(entry.getValue().player,
                    ChatColor.DARK_GRAY + "The plugin is being disabled due to a server reload/restart/shutdown!" +
                            " Kicking you from the game...");
            entry.getValue().leaveGame(false);
        }
        for (Map.Entry<String, CreateState> entry : Tracker.creationStatus.entrySet()) {
            Util.sendMessage(entry.getValue().player,
                    ChatColor.DARK_GRAY + "The plugin is being disabled due to a server reload/restart/shutdown!" +
                            " Kicking you from map creation...");
            entry.getValue().beforeGameState.restoreState(false);
        }
        EventHandler.saveDDashData(false);

        Tracker.creationStatus.clear();
        Tracker.maps.clear();
        Tracker.playStatus.clear();
        Tracker.playStatusesToRemove.clear();
        Tracker.lobbies.clear();
        Tracker.lobbiesToRemove.clear();
        Tracker.beforeGameStates.clear();
        Tracker.guis.clear();
        Tracker.playerStorage.clear();
        Tracker.currentTasks.clear();
    }
}
