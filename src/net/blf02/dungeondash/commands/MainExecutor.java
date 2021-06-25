package net.blf02.dungeondash.commands;

import net.blf02.dungeondash.DungeonDash;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainExecutor implements CommandExecutor {

    public static final String chatTag = "[DungeonDash] ";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        // Requires dungeondash.any to use
        if (args.length < 1) {
            return help(commandSender);
        } else if (args[0].equals("help")) {
            return help(commandSender);
        } else if (args[0].equals("version")) {
            return version(commandSender);
        }
        return false;
    }

    public boolean version(CommandSender sender) {
        sendMessage(sender, "Version " + DungeonDash.VERSION);
        return true;
    }

    public boolean help(CommandSender sender) {
        sendMessage(sender, "TODO!");
        return true;
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(chatTag + message);
    }
}
