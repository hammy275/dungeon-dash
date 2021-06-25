package net.blf02.dungeondash.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionChecker {

    public static final String allPermission = "dungeondash.any";
    public static final String joinGamePermission = "dungeondash.join";
    public static final String createMapPermission = "dungeondash.create";

    public static boolean hasPermission(CommandSender sender, String[] args) {
        if (sender instanceof Player && args.length >= 1) {
            Player player = (Player) sender;
            if (player.isOp()) { // The all-powerful operator!
                return true;
            }
            if (!player.hasPermission(allPermission)) {
                return false;
            } else if (args[0].equals("play") && !player.hasPermission(joinGamePermission)) {
                return false;
            } else if (args[0].equals("create") && !player.hasPermission(createMapPermission)) {
                return false;
            }
            return true;

        }
        return true;
    }

}