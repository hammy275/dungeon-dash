package net.blf02.dungeondash.utils;

import net.blf02.dungeondash.commands.MainExecutor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Util {
    public static void sendMessage(@Nullable CommandSender sender, String message) {
        if (sender != null) {
            sender.sendMessage(MainExecutor.chatTag + message);
        }
    }

    public static void sendMessage(@Nullable CommandSender sender, String[] messages) {
        if (sender == null) return;
        String[] prefixedMessages = new String[messages.length];
        for (int i = 0; i < messages.length; i++) {
            prefixedMessages[i] = MainExecutor.chatTag + messages[i];
        }
        sender.sendMessage(prefixedMessages);
    }

    public static void sendMessage(@Nullable Player player, String message) {
        if (player != null) {
            player.sendMessage(MainExecutor.chatTag + message);
        }
    }

    public static void sendMessage(@Nullable Player player, String[] messages) {
        if (player == null) return;
        String[] prefixedMessages = new String[messages.length];
        for (int i = 0; i < messages.length; i++) {
            prefixedMessages[i] = MainExecutor.chatTag + messages[i];
        }
        player.sendMessage(prefixedMessages);
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
