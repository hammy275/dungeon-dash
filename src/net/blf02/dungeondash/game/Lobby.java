package net.blf02.dungeondash.game;

import java.util.ArrayList;
import java.util.List;

public class Lobby {

    public boolean gameStarted = false;
    public List<PlayerState> playerStates = new ArrayList<>();
    public int ticksUntilStart = 20*30;
}
