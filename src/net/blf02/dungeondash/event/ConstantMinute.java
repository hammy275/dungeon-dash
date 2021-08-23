package net.blf02.dungeondash.event;

import net.blf02.dungeondash.utils.PlayerStorage;
import net.blf02.dungeondash.utils.Tracker;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ConstantMinute {

    public static void handleEveryMinute() {
        savePlayerStorages();
    }

    public static void savePlayerStorages() {
        Set<UUID> toRemove = new HashSet<>();
        for (Map.Entry<UUID, PlayerStorage> entry : Tracker.playerStorage.entrySet()) {
            entry.getValue().save();
            entry.getValue().minutesSinceLastAccess++;
            if (entry.getValue().minutesSinceLastAccess >= 5) {
                toRemove.add(entry.getKey());
            }
        }
        for (UUID key : toRemove) { // Remove PlayerStorage's from our "cache' after 5 minutes.
            Tracker.playerStorage.remove(key);
        }
    }
}
