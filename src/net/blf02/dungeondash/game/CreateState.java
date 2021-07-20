package net.blf02.dungeondash.game;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class CreateState {

    public DDMap map;
    public Player player;
    public BeforeGameState beforeGameState;

    public CreateState(Player player, DDMap map) {
        this.map = map;
        this.player = player;
        this.beforeGameState = new BeforeGameState(player);
        player.setGameMode(GameMode.CREATIVE);
        // Wipe hotbar
        for (int i = 0; i < 9; i++) {
            player.getInventory().clear(i);
        }
    }
}
