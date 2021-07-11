package net.blf02.dungeondash.utils;

import net.blf02.dungeondash.commands.MainExecutor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Util {

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
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) == '`') {
                if (backQuotes % 2 == 0) {
                    toRet.append(ChatColor.YELLOW);
                } else {
                    toRet.append(ChatColor.GRAY);
                }
                backQuotes++;
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
            prefixedMessages[i] = MainExecutor.chatTag + processMessage(messages[i]);
        }
        sender.sendMessage(prefixedMessages);
    }


    public static ArmorStand spawnChaser(Location location) {
        Objects.requireNonNull(location.getWorld());
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawn(location, EntityType.ARMOR_STAND.getEntityClass());
        armorStand.setInvulnerable(true);
        armorStand.setInvisible(true);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setMarker(true);
        return armorStand;
    }
}
