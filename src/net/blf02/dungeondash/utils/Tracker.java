package net.blf02.dungeondash.utils;


import net.blf02.dungeondash.game.DDMap;

import java.util.*;

public class Tracker {

    // Usernames don't change without rejoining, so no need to use UUIDs here.
    public static final List<String> inGamePlayers = new ArrayList<>();

    // Tracks each player's "creating a map" status
    public static final Map<String, DDMap> creationStatus = new HashMap<>();

    // List of all available maps
    public static final Set<DDMap> maps = new HashSet<>();

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
