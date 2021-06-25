package net.blf02.dungeondash.commands;

import net.blf02.dungeondash.game.DDMap;
import net.blf02.dungeondash.utils.Tracker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class CreateHandler {

    public static final String[] helpMsg = new String[]{
            "/ddash create help: Displays this help message",
            "/ddash create [Map Name]: Begins creating a map with the name 'Map Name'",
            "/ddash create spawn: Run after creating a map to set the starting point for players.",
            "/ddash create end1: Run after the above command to set the first corner of the ending zone.",
            "/ddash create end2: Run after the above command to set the second corner for the ending zone.",
            "/ddash create cancel: Run during any point of the creation process to cancel creation."
    };

    public static void handleCreateCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MainExecutor.sendMessage(sender, "Only execute create commands as a player!");
        }
        Player player = (Player) sender;
        if (args[1].equals("help")) {
            MainExecutor.sendMessage(sender, helpMsg);
        } else if (Tracker.creationStatus.get(player.getDisplayName()) == null) {
            if (Tracker.getMap(args[1]) != null) {
                MainExecutor.sendMessage(sender, "That map already exists!");
            }
            Tracker.creationStatus.put(player.getDisplayName(),
                    new DDMap(args[1], null, null, null));
            sender.sendMessage("Created map with name '" + args[1] + "'! Type `/ddash create spawn` to set the spawnpoint for this map, or `/ddash create cancel` to cancel creation!");
        } else if (args[1].equals("spawn")) {
            DDMap old = Tracker.creationStatus.get(player.getDisplayName());
            Tracker.creationStatus.put(player.getDisplayName(),
                    new DDMap(old.mapDisplayName, player.getLocation(), old.endCorner1, old.endCorner2));
            sender.sendMessage("Set start position of map! Type `/ddash create end1` to set the first corner of the ending zone!");
        } else if (args[1].equals("end1")) {
            DDMap old = Tracker.creationStatus.get(player.getDisplayName());
            Tracker.creationStatus.put(player.getDisplayName(),
                    new DDMap(old.mapDisplayName, old.start, player.getLocation(), old.endCorner2));
            sender.sendMessage("Set first corner of end! Type `/ddash create end2` to set the other corner of the ending zone!");
        } else if (args[1].equals("end2")) {
            DDMap old = Tracker.creationStatus.get(player.getDisplayName());
            Tracker.creationStatus.put(player.getDisplayName(),
                    new DDMap(old.mapDisplayName, old.start, old.endCorner1, player.getLocation()));
            sender.sendMessage("Set second corner of the map successfully! Saving map...");
            DDMap map = Tracker.creationStatus.get(player.getDisplayName());
            if (!map.isFullMap) {
                sender.sendMessage("Error: Map is missing all properties! Did you perform the steps out of order?");
            }
            boolean res = map.saveMap(sender);
            if (res) {
                sender.sendMessage("Map saved successfully!");
                Tracker.maps.add(map);
                Tracker.creationStatus.remove(player.getDisplayName());
            }
        }
        MainExecutor.sendMessage(sender, "Invalid subcommand! Type /ddash create help for help!");
    }
}
