package net.blf02.dungeondash.config;

import org.bukkit.ChatColor;

public class Constants {

    public static final String scoreboardTag = ChatColor.GRAY + "[" + ChatColor.RED + "Dungeon"
            + ChatColor.BLUE + "Dash" + ChatColor.GRAY + "]";
    public static final String chatTag = scoreboardTag + ChatColor.RESET + " ";

    public static final String VERSION = "1.2.0";

    public static final double[] chaserSpeeds = new double[]{Config.distanceToMoveSlow, Config.distanceToMoveNormal, Config.distanceToMoveFast};
    public static final double[] shadowSpeeds = new double[]{Config.shadowPercentSlow, Config.shadowPercentNormal, Config.shadowPercentFast};
}
