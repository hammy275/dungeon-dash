package net.blf02.dungeondash.utils;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    public static FileConfiguration config = null;

    public static boolean RESTORE_INVENTORIES = true;

    public static void addDefaultOptions() {
        config.addDefault("restoreInventoriesAfterCrash", true);
    }

    public static void readOptions() {
        RESTORE_INVENTORIES = config.getBoolean("restoreInventoriesAfterCrash");
    }
}
