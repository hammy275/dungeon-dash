package net.blf02.dungeondash.event;

import net.blf02.dungeondash.commands.MainExecutor;
import net.blf02.dungeondash.config.Config;
import net.blf02.dungeondash.game.PlayerState;
import net.blf02.dungeondash.utils.Tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConstantTick {

    public static void handleEveryTick() {
        handlePlayersPlaying();
    }

    public static void handlePlayersPlaying() {
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, PlayerState> entry : Tracker.playStatus.entrySet()) {
            entry.getValue().noStillCheck();
            if (entry.getValue().isInEndingZone()) {
                toRemove.add(entry.getKey());
                MainExecutor.sendMessage(entry.getValue().player, "You win!");
                entry.getValue().player.teleport(entry.getValue().locationBeforePlaying);
            } else if (entry.getValue().ticksStill >= Config.ticksStillUntilDeath) {
                toRemove.add(entry.getKey());
                MainExecutor.sendMessage(entry.getValue().player, "You lose!");
                entry.getValue().player.teleport(entry.getValue().locationBeforePlaying);
            }
        }
        for (String s : toRemove) {
            Tracker.playStatus.remove(s);
        }
    }
}
