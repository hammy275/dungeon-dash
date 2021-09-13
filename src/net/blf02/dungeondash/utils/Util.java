package net.blf02.dungeondash.utils;

import net.blf02.dungeondash.DungeonDash;
import net.blf02.dungeondash.config.Constants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

public class Util {

    /**
     * Run a function with access to a player's PlayerStorage instance.
     *
     * Can execute anywhere from a tick from now to several seconds from now due to requiring disk I/O!
     *
     * @param player Player to run on.
     * @param toRun Runnable. Should get the PlayerStorage from Tracker.playerStorage.
     */
    public static void runWithPlayerStorage(Player player, Runnable toRun) {
        doAsync(() -> PlayerStorage.loadPlayerStorage(player), toRun);
    }

    /**
     * Run an async task with sync after.
     *
     * @param task The function to run async.
     * @param after The function to run after that's sync.
     */
    public static void doAsync(Runnable task, Runnable after) {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskAsynchronously(DungeonDash.instance, task);
        Tracker.currentTasks.add(new TaskWithAfter(bukkitTask, after));
    }

    /**
     * Processes a Message to use Color-Code Formatting.
     *
     * @param message Message to process
     * @return Processed message.
     */
    public static String processMessage(String message) {
        StringBuilder toRet = new StringBuilder();
        // All messages are gray by default
        toRet.append(ChatColor.GRAY);

        // Anything in `'s are commands and should be yellow
        int backQuotes = 0;
        int equals = 0;
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) == '`') {
                if (backQuotes % 2 == 0) {
                    toRet.append(ChatColor.YELLOW);
                } else {
                    toRet.append(ChatColor.GRAY);
                }
                backQuotes++;
            } else if (message.charAt(i) == '=') {
                if (equals % 2 == 0) {
                    toRet.append(ChatColor.GREEN);
                } else {
                    toRet.append(ChatColor.GRAY);
                }
                equals++;
            } else {
                toRet.append(message.charAt(i));
            }
        }
        return toRet.toString();
    }

    public static void sendMessage(@Nullable CommandSender sender, String message) {
        if (sender != null) {
            Util.sendMessage(sender, new String[]{message});
        }
    }

    public static void sendMessage(@Nullable CommandSender sender, String[] messages) {
        if (sender == null) return;
        String[] prefixedMessages = new String[messages.length];
        for (int i = 0; i < messages.length; i++) {
            prefixedMessages[i] = Constants.chatTag + processMessage(messages[i]);
        }
        sender.sendMessage(prefixedMessages);
    }


    public static ArmorStand spawnChaser(Location location, String name) {
        Objects.requireNonNull(location.getWorld());
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawn(location, EntityType.ARMOR_STAND.getEntityClass());
        armorStand.setInvulnerable(true);
        armorStand.setInvisible(true);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setMarker(true);
        armorStand.setCustomName(name);
        armorStand.setCustomNameVisible(false);
        return armorStand;
    }
    public static ArmorStand spawnChaser(Location location) {
        return spawnChaser(location, "Chaser");
    }

}
