package net.blf02.dungeondash.utils;


import net.blf02.dungeondash.game.*;
import net.blf02.dungeondash.inventory.BaseGUI;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

public class Tracker {

    // Scoreboard
    public static ScoreboardManager manager = null;

    // Tracks each player's "creating a map" status
    public static final Map<String, CreateState> creationStatus = new HashMap<>();

    // List of all available maps
    public static final Set<DDMap> maps = new HashSet<>();

    // Tracks each player's "playing a game" status.
    public static final Map<String, PlayerState> playStatus = new HashMap<>();

    // All player display names in here will be removed from playStatus ASAP
    public static List<String> playStatusesToRemove = new ArrayList<>();

    // Maps can have respective lobbies
    public static final Map<DDMap, Lobby> lobbies = new HashMap<>();

    // Lobbies to remove
    public static final List<DDMap> lobbiesToRemove = new LinkedList<>();

    // BeforeGameStates to re-apply (used to prevent a crash in EventHandler)
    public static final Set<BeforeGameState> beforeGameStates = new LinkedHashSet<>();

    // Currently active GUIs
    public static final Map<Inventory, BaseGUI> guis = new HashMap<>();

    // Currently loaded PlayerStorage's
    public static final Map<UUID, PlayerStorage> playerStorage = new HashMap<>();

    // Current async tasks
    public static final Set<TaskWithAfter> currentTasks = new HashSet<>();

    // Get Map by name.
    public static DDMap getMap(String mapName) {
        for (DDMap m : maps) {
            if (m.mapDisplayName.equalsIgnoreCase(mapName)) {
                return m;
            }
        }
        return null;
    }

}
