package net.blf02.dungeondash.event;

import net.blf02.dungeondash.game.DDMap;
import net.blf02.dungeondash.game.Lobby;
import net.blf02.dungeondash.game.PlayerState;
import net.blf02.dungeondash.utils.Tracker;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Score;

import java.util.Map;

public class ConstantSecond {

    public static void handleEverySecond() {
        updateLobbies();
    }

    public static void updateLobbies() {
        // Set rankings
        for (Map.Entry<DDMap, Lobby> entry : Tracker.lobbies.entrySet()) {
            entry.getValue().resetRankings();
            // Puts rankings in first to last order
            PlayerState[] rankings = new PlayerState[entry.getValue().positionsLastToFirst.size()];
            int sizeCounter = entry.getValue().positionsLastToFirst.size();
            for (PlayerState p : entry.getValue().positionsLastToFirst) {
                rankings[--sizeCounter] = p;
            }
            for (PlayerState p : entry.getValue().playerStates) {
                for (String scoreboardEntry : p.scoreboard.getEntries()) {
                    p.scoreboard.resetScores(scoreboardEntry);
                }
                if (p.inLobby) {
                    Score score = p.objective.getScore(ChatColor.GREEN + "In Lobby");
                    score.setScore(16);
                    Score timeUntilStart = p.objective.getScore(ChatColor.AQUA + "Game will begin in...");
                    timeUntilStart.setScore(15);
                    Score startTime = p.objective.getScore(entry.getValue().ticksUntilStart / 20 + " seconds!");
                    startTime.setScore(14);
                } else {
                    int i = 16;

                    Score rankingScore = p.objective.getScore(ChatColor.WHITE + (ChatColor.BOLD + "Rankings:"));
                    rankingScore.setScore(i--);

                    Score first = p.objective.getScore(ChatColor.AQUA + (ChatColor.BOLD + "1: ") + rankings[0].player.getDisplayName());
                    first.setScore(i--);
                    if (rankings.length >= 2) {
                        Score second = p.objective.getScore(ChatColor.GREEN + "2: " + rankings[1].player.getDisplayName());
                        second.setScore(i--);
                    }
                    if (rankings.length >= 3) {
                        Score third = p.objective.getScore(ChatColor.YELLOW + "3: " + rankings[2].player.getDisplayName());
                        third.setScore(i--);
                    }
                    if (rankings.length >= 4) {
                        Score fourth = p.objective.getScore(ChatColor.YELLOW + "4: " + rankings[3].player.getDisplayName());
                        fourth.setScore(i--);
                    }

                    Score dots = p.objective.getScore(ChatColor.BOLD + "-----");
                    dots.setScore(i--);

                    int place = -1;

                    for (int j = 0; j < rankings.length; j++) {
                        if (rankings[j] == p) {
                            place = j + 1;
                        }
                    }

                    Score playerScore = p.objective.getScore(String.format(ChatColor.BOLD + "%d: "
                                    + p.player.getDisplayName(), place));
                    playerScore.setScore(i--);
                }
            }
        }
    }

}
