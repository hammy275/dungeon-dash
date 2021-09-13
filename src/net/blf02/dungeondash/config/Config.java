package net.blf02.dungeondash.config;

public class Config {

    public static final int ticksBetweenMove = 1;
    public static final int ticksInLobby = 20*30;

    public static final double distanceToMoveNormal = 0.147;
    public static final double distanceToMoveSlow = distanceToMoveNormal * distanceToMoveNormal * 0.5;
    public static final double distanceToMoveFast = distanceToMoveNormal * 1.5;

    public static final double shadowPercentSlow = 0.5;
    public static final double shadowPercentNormal = 0.75;
    public static final double shadowPercentFast = 0.925;

}
