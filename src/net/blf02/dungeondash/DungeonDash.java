package net.blf02.dungeondash;

import net.blf02.dungeondash.commands.MainExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class DungeonDash extends JavaPlugin {

    public static final String VERSION = "0.1.0";

    @Override
    public void onEnable() {
        super.onEnable();
        // Master command to handle everything
        this.getCommand("ddash").setExecutor(new MainExecutor());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
