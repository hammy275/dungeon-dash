package net.blf02.dungeondash.utils;


import net.blf02.dungeondash.game.DDMap;
import net.blf02.dungeondash.game.PlayerState;

import java.util.*;

public class Tracker {

    // Tracks each player's "creating a map" status
    public static final Map<String, DDMap> creationStatus = new HashMap<>();

    // List of all available maps
    public static final Set<DDMap> maps = new HashSet<>();

    // Tracks each player's "playing a game" status.
    public static final Map<String, PlayerState> playStatus = new HashMap<>();

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
