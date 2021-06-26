package net.blf02.dungeondash.commands;

import net.blf02.dungeondash.DungeonDash;
import net.blf02.dungeondash.game.DDMap;
import net.blf02.dungeondash.game.PlayerState;
import net.blf02.dungeondash.utils.PermissionChecker;
import net.blf02.dungeondash.utils.Tracker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MainExecutor implements CommandExecutor {

    public static final String chatTag = "[DungeonDash] ";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!PermissionChecker.hasPermission(commandSender, args)) {
            sendMessage(commandSender, "You don't have permission to execute this command!");
            return true;
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
        } else if (args[0].equals("leave")) {
            leaveGame(commandSender);
        } else if (args[0].equals("list")) {
            String[] msg = new String[Tracker.maps.size() + 1];
            msg[0] = "Available maps: ";
            int i = 1;
            for (DDMap m : Tracker.maps) {
                msg[i++] = m.mapDisplayName;
            }
            sendMessage(commandSender, msg);
        } else if (args[0].equals("remove")) {
            if (args.length < 2) {
                sendMessage(commandSender, "Missing the name of the map to remove!");
            } else {
                removeMap(commandSender, args[1]);
            }
        }
        return true;
    }

    public void removeMap(CommandSender sender, String mapName) {
        for (DDMap map : Tracker.maps) {
            if (map.mapDisplayName.equals(mapName)) {
                Tracker.maps.remove(map);
                File mapFile = new File(DungeonDash.mapsDir + map.mapDisplayName + ".data");
                if (mapFile.delete()) {
                    sendMessage(sender, "Successfully deleted map!");
                } else {
                    sendMessage(sender, "Failed to delete map, it will become available again once the " +
                            "server restarts! Maybe ask the server owner to delete it manually?");
                }
                return;
            }
        }
        sendMessage(sender, "Map not found!");
    }


    public void leaveGame(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerState state = Tracker.playStatus.get(player.getDisplayName());
            if (state == null) {
                sendMessage(sender, "You aren't in a game right now!");
            } else {
                player.teleport(state.locationBeforePlaying);
                Tracker.playStatus.remove(player.getDisplayName());
                sendMessage(sender, "You left the game!");
            }
        } else {
            sendMessage(sender, "You aren't a player! How can you leave a game if you can't join one?");
        }
    }

    public void joinGame(CommandSender sender, String mapName) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            DDMap map = Tracker.getMap(mapName);
            if (map == null) {
                sendMessage(sender, "That map dos not exist!");
            } else {
                player.teleport(map.start);
                Tracker.playStatus.put(player.getDisplayName(), new PlayerState(map, player));
            }
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
        if (sender == null) return;
        String[] prefixedMessages = new String[messages.length];
        for (int i = 0; i < messages.length; i++) {
            prefixedMessages[i] = chatTag + messages[i];
        }
        sender.sendMessage(prefixedMessages);
    }

    public static void sendMessage(@Nullable Player player, String message) {
        if (player != null) {
            player.sendMessage(chatTag + message);
        }
    }

    public static void sendMessage(@Nullable Player player, String[] messages) {
        if (player == null) return;
        String[] prefixedMessages = new String[messages.length];
        for (int i = 0; i < messages.length; i++) {
            prefixedMessages[i] = chatTag + messages[i];
        }
        player.sendMessage(prefixedMessages);
    }
}
