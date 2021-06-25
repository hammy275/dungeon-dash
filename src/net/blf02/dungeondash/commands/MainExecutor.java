package net.blf02.dungeondash.commands;

import net.blf02.dungeondash.DungeonDash;
import net.blf02.dungeondash.game.DDMap;
import net.blf02.dungeondash.utils.PermissionChecker;
import net.blf02.dungeondash.utils.Tracker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;
import org.bukkit.entity.Player;

public class MainExecutor implements CommandExecutor {

    public static final String chatTag = "[DungeonDash] ";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!PermissionChecker.hasPermission(commandSender, args)) {
            sendMessage(commandSender, "You don't have permission to execute this command!");
        }
        if (args.length < 1) {
            help(commandSender);
        } else if (args[0].equals("help")) {
            help(commandSender);
        } else if (args[0].equals("version")) {
            version(commandSender);
        } else if (args[0].equals("play")) {
            if (args.length < 2) {
                sendMessage(commandSender, "Please specify a lobby to join!");
            } else {
                joinGame(commandSender, args[1]);
            }
        } else if (args[0].equals("create")) {
            if (args.length < 2) {
                sendMessage(commandSender, "Missing a map name or subcommand!");
            } else {
                CreateHandler.handleCreateCommand(commandSender, args);
            }
        }
        return true;
    }

    public void joinGame(CommandSender sender, String mapName) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            DDMap map = Tracker.getMap(mapName);
            if (map == null) {
                sendMessage(sender, "That map dos not exist!");
            }
            player.teleport(map.start);
        } else {
            sendMessage(sender, "You cannot join a game unless you are a player!");
        }
    }

    public void version(CommandSender sender) {
        sendMessage(sender, "Version " + DungeonDash.VERSION);
    }

    public void help(CommandSender sender) {
        sendMessage(sender, "TODO!");
    }

    public static void sendMessage(@Nullable CommandSender sender, String message) {
        if (sender != null) {
            sender.sendMessage(chatTag + message);
        }
    }

    public static void sendMessage(@Nullable CommandSender sender, String[] messages) {
        String[] prefixedMessages = new String[messages.length];
        for (int i = 0; i < messages.length; i++) {
            prefixedMessages[i] = chatTag + messages[i];
        }
        sender.sendMessage(prefixedMessages);
    }
}
