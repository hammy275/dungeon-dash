package net.blf02.dungeondash.commands;

import net.blf02.dungeondash.DungeonDash;
import net.blf02.dungeondash.config.Constants;
import net.blf02.dungeondash.game.CreateState;
import net.blf02.dungeondash.game.DDMap;
import net.blf02.dungeondash.game.Lobby;
import net.blf02.dungeondash.game.PlayerState;
import net.blf02.dungeondash.inventory.JoinGameGUI;
import net.blf02.dungeondash.utils.PermissionChecker;
import net.blf02.dungeondash.utils.Tracker;
import net.blf02.dungeondash.utils.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class MainExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!PermissionChecker.hasPermission(commandSender, args)) {
            Util.sendMessage(commandSender, "You don't have permission to execute this command!");
            return true;
        }
        if (args.length < 1) {
            help(commandSender);
        } else if (args[0].equals("help")) {
            help(commandSender);
        } else if (args[0].equals("version")) {
            version(commandSender);
        } else if (args[0].equals("play") || args[0].equals("join")) {
            if (args.length < 2) {
                if (commandSender instanceof Player) {
                    JoinGameGUI gui = new JoinGameGUI();
                    gui.openOnPlayer((Player) commandSender);
                } else {
                    Util.sendMessage(commandSender, "You are not a player!");
                }
            } else {
                joinGame(commandSender, args[1]);
            }
        } else if (args[0].equals("create")) {
            if (args.length < 2) {
                Util.sendMessage(commandSender, "Missing a map name or subcommand!");
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
            Util.sendMessage(commandSender, msg);
        } else if (args[0].equals("remove")) {
            if (args.length < 2) {
                Util.sendMessage(commandSender, "Missing the name of the map to remove!");
            } else {
                removeMap(commandSender, args[1]);
            }
        } else if (args[0].equals("edit")) {
            if (args.length < 2) {
                Util.sendMessage(commandSender, "Missing the name of the map to edit!");
            } else {
                editMap(commandSender, args[1]);
            }
        } else {
            Util.sendMessage(commandSender, "Invalid command! Type /ddash help for help!");
        }
        return true;
    }

    public void editMap(CommandSender sender, String mapName) {
        if (!(sender instanceof Player)) {
            Util.sendMessage(sender, "You can't edit a map as a non-player!");
        } else {
            Player player = (Player) sender;
            if (Tracker.playStatus.get(player.getDisplayName()) != null) {
                Util.sendMessage(player, "You're currently playing a map! Please use `/ddash leave` to leave, first!");
                return;
            } else if (Tracker.creationStatus.get(player.getDisplayName()) != null) {
                Util.sendMessage(player, "You're already creating or editing a map!");
                return;
            }
            for (DDMap map : Tracker.maps) {
                if (map.mapDisplayName.equals(mapName)) {
                    Tracker.creationStatus.put(player.getDisplayName(), new CreateState(player, map, map));
                    map.isFullMap = true;
                    Util.sendMessage(player, "Editing map =" + map.mapDisplayName + "=.");
                    return;
                }
            }
            Util.sendMessage(sender, "Could not find map =" + mapName + "=!");
        }
    }

    public void removeMap(CommandSender sender, String mapName) {
        for (DDMap map : Tracker.maps) {
            if (map.mapDisplayName.equals(mapName)) {
                Tracker.maps.remove(map);
                File mapFile = new File(DungeonDash.mapsDir + map.mapDisplayName + ".data");
                if (mapFile.delete()) {
                    Util.sendMessage(sender, "Successfully deleted map!");
                } else {
                    Util.sendMessage(sender, "Failed to delete map, it will become available again once the " +
                            "server restarts! Maybe ask the server owner to delete it manually?");
                }
                return;
            }
        }
        Util.sendMessage(sender, "Map not found!");
    }


    public void leaveGame(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerState state = Tracker.playStatus.get(player.getDisplayName());
            if (state == null) {
                Util.sendMessage(sender, "You aren't in a game right now!");
            } else {
                state.leaveGame(true);
                Util.sendMessage(sender, "You left the game!");
            }
        } else {
            Util.sendMessage(sender, "You aren't a player! How can you leave a game if you can't join one?");
        }
    }

    public void joinGame(CommandSender sender, String mapName) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            DDMap map = Tracker.getMap(mapName);
            if (map == null) {
                Util.sendMessage(sender, "That map dos not exist!");
            } else if (Tracker.playStatus.get(player.getDisplayName()) != null) {
              Util.sendMessage(sender, "You are already in a game or lobby! Use `/ddash leave` to leave!");
            } else if (Tracker.creationStatus.get(player.getDisplayName()) != null) {
                Util.sendMessage(sender, "You are currently creating/editing a map!");
            } else {
                Lobby lobby = Tracker.lobbies.get(map);
                if (lobby == null) {
                    lobby = new Lobby();
                    Tracker.lobbies.put(map, lobby);
                } else if (lobby.gameStarted) {
                    Util.sendMessage(sender, "That map's game has already started!");
                    return;
                }
                PlayerState state = new PlayerState(map, player, lobby);
                Tracker.playStatus.put(player.getDisplayName(), state);
                lobby.playerStates.add(state);
            }
        } else {
            Util.sendMessage(sender, "You cannot join a game unless you are a player!");
        }
    }

    public void version(CommandSender sender) {
        Util.sendMessage(sender, "Version " + Constants.VERSION);
    }

    public void help(CommandSender sender) {
        // Yellow - Light Gray
        Util.sendMessage(sender, new String[]{
                "`/ddash help` - View this help command.",
                "`/ddash version` - View the version of DungeonDash this server is using.",
                "`/ddash play (Map Name)` - Play the specified map name",
                "`/ddash create` - DungeonDash map creator. Type /ddash create help for information on using it.",
                "`/ddash leave` - Leave a game or lobby of DungeonDash.",
                "`/ddash list` - List all available DungeonDash maps.",
                "`/ddash remove (Map Name)` - Remove a DungeonDash map.",
                "`/ddash edit (Map Name)` - Edit a DungeonDash map."
        });
    }

}
