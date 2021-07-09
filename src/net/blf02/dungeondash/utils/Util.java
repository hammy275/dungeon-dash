package net.blf02.dungeondash.utils;

import net.blf02.dungeondash.commands.MainExecutor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;
import org.bukkit.entity.*;

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

    public static EnderDragon spawnChaser(Location location) {
        Objects.requireNonNull(location.getWorld());
        EnderDragon enderDragon = (EnderDragon) location.getWorld().spawn(location, EntityType.ENDER_DRAGON.getEntityClass());
        enderDragon.setInvulnerable(true);
        enderDragon.setGravity(false);
        AttributeInstance a = enderDragon.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
        if (a != null) {
            a.setBaseValue(256);
        }
        // Prevent EDragon from destroying blocks
        enderDragon.getWorld().setGameRule(GameRule.MOB_GRIEFING, false);
        return enderDragon;
    }
}
