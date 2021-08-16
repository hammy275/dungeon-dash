package net.blf02.dungeondash.commands;

import net.blf02.dungeondash.game.CreateState;
import net.blf02.dungeondash.game.DDMap;
import net.blf02.dungeondash.utils.Tracker;
import net.blf02.dungeondash.utils.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateHandler {

    public static final String[] helpMsg = new String[]{
            "`/ddash create help` - Displays this help message",
            "`/ddash create [Map Name]` - Begins creating a map with the name 'Map Name'",
            "`/ddash create spawn` (=Lime Dye=) - Run after creating a map to set the starting point for players.",
            "`/ddash create end1` (=Red Dye=) - Run after the above command to set the first corner of the ending zone where you're standing.",
            "`/ddash create end2` (=Red Dye=) - Run after the above command to set the second corner for the ending zone where you're standing.",
            "`/ddash create void_respawn` (=Bedrock=) - Enables/disables the void respawning players. Defaults to false.",
            "`/ddash create water_respawn` (=Water Bucket=) - Enables/disables water respawning players. Defaults to false.",
            "`/ddash create respawns_kill` (=Totem of Undying=) - Enables/disables respawns killing the player instead. Defaults to false.",
            "`/ddash create use_chaser` (=Armor Stand=) - Enables/disables the chaser. If not enabled, first to finish wins! Defaults to true.",
            "`/ddash create save` (=Command Block=) - Saves the map to disk, and makes it available to players to play.",
            "`/ddash create cancel` (=Barrier in Rightmost Hotbar Slot=) - Run during any point of the creation process to cancel creation."
    };

    public static void handleCreateCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Util.sendMessage(sender, "Only execute create commands as a player!");
            return;
        }
        Player player = (Player) sender;
        if (args[1].equals("help")) {
            Util.sendMessage(sender, helpMsg);
        } else if (Tracker.creationStatus.get(player.getDisplayName()) == null) {
            if (Tracker.getMap(args[1]) != null) {
                Util.sendMessage(sender, "That map already exists!");
            } else {
                Tracker.creationStatus.put(player.getDisplayName(),
                        new CreateState(player, new DDMap(args[1], null, null, null)));
                Util.sendMessage(sender, "Created map with name '" + args[1] + "'! Type `/ddash create spawn` or use the =Lime Dye= to set the spawnpoint for this map, or `/ddash create cancel` to cancel creation!");
            }
        } else if (args[1].equals("spawn")) {
            CreateState old = Tracker.creationStatus.get(player.getDisplayName());
            old.map = new DDMap(old.map.mapDisplayName, player.getLocation(), old.map.endCorner1, old.map.endCorner2);
            Util.sendMessage(sender, "Set start position of map! Type `/ddash create end1` or use the =Red Dye= to set the first corner of the ending zone!");
        } else if (args[1].equals("end1")) {
            CreateState old = Tracker.creationStatus.get(player.getDisplayName());
            old.map = new DDMap(old.map.mapDisplayName, old.map.start, player.getLocation(), old.map.endCorner2);
            old.currentCornerItem = 2;
            Util.sendMessage(sender, "Set first corner of end! Type `/ddash create end2` or use the =Red Dye= to set the other corner of the ending zone!");
        } else if (args[1].equals("end2")) {
            CreateState old = Tracker.creationStatus.get(player.getDisplayName());
            old.map = new DDMap(old.map.mapDisplayName, old.map.start, old.map.endCorner1, player.getLocation());
            old.currentCornerItem = 1;
            Util.sendMessage(sender, "Set second corner of the map successfully! Use `/ddash create help` to see what other commands and items you can run, or scroll around your hotbar!");
        } else if (args[1].equals("save")) {
            CreateState state = Tracker.creationStatus.get(player.getDisplayName());
            DDMap map = state.map;
            if (!map.isFullMap) {
                Util.sendMessage(sender, "Error: Map is missing needed properties! Make sure you've set the 'spawn' and both `end corners`!");
                return;
            }
            boolean res = map.saveMap(sender);
            if (res) {
                Util.sendMessage(sender, "Map saved successfully!");
                Tracker.maps.add(map);
                Tracker.beforeGameStates.add(state.beforeGameState);
                Tracker.creationStatus.remove(player.getDisplayName());
            }
        } else if (args[1].equals("void_respawns") || args[1].equals("void_respawn")) {
            CreateState state = Tracker.creationStatus.get(player.getDisplayName());
            DDMap map = state.map;
            map.voidRespawns = !map.voidRespawns;
            if (map.voidRespawns) {
                Util.sendMessage(sender, "Players entering the void now respawn!");
            } else {
                Util.sendMessage(sender, "Players entering the void no longer respawn!");
            }
        } else if (args[1].equals("water_respawns") || args[1].equals("water_respawn")) {
            CreateState state = Tracker.creationStatus.get(player.getDisplayName());
            DDMap map = state.map;
            map.waterRespawns = !map.waterRespawns;
            if (map.waterRespawns) {
                Util.sendMessage(sender, "Players entering water now respawn!");
            } else {
                Util.sendMessage(sender, "Players entering water no longer respawn!");
            }
        } else if (args[1].equals("respawns_kill") || args[1].equals("respawn_kill") || args[1].equals("respawn_kills")) {
            CreateState state = Tracker.creationStatus.get(player.getDisplayName());
            DDMap map = state.map;
            map.respawnsKill = !map.respawnsKill;
            if (map.respawnsKill) {
                Util.sendMessage(sender, "Players are now killed instead of respawning!");
            } else {
                Util.sendMessage(sender, "Players now respawn normally!");
            }
        } else if (args[1].equals("use_chaser")) {
            CreateState state = Tracker.creationStatus.get(player.getDisplayName());
            DDMap map = state.map;
            map.hasChaser = !map.hasChaser;
            if (map.hasChaser) {
                Util.sendMessage(sender, "Players are now chased throughout the map!");
            } else {
                Util.sendMessage(sender, "Players are no longer chased! First to finish wins!");
            }
        } else if (args[1].equals("cancel")) {
            CreateState state = Tracker.creationStatus.get(player.getDisplayName());
            if (Tracker.creationStatus.remove(player.getDisplayName()) != null) {
                Tracker.beforeGameStates.add(state.beforeGameState);
                Util.sendMessage(sender, "Cancelled creation!");
            } else {
                Util.sendMessage(sender, "Could not cancel creation process as one was not started!");
            }
        }
        else {
            Util.sendMessage(sender, "Invalid subcommand! Type `/ddash create help` for help!");
        }
    }
}
