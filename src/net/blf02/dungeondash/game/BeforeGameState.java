package net.blf02.dungeondash.game;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class BeforeGameState {

    public Player player;

    public Location location;
    public double health;
    public int food;
    public float saturation;
    public ItemStack[] inventory;
    public GameMode gamemode;
    public Collection<PotionEffect> potionEffects;

    public BeforeGameState(Player player) {
        this.player = player;

        this.location = player.getLocation();
        this.health = player.getHealth();
        this.food = player.getFoodLevel();
        this.saturation = player.getSaturation();
        this.inventory = player.getInventory().getContents();
        this.gamemode = player.getGameMode();
        this.potionEffects = player.getActivePotionEffects();
    }

    public void restoreState() {
        player.setFallDistance(0);
        player.teleport(location);
        player.setFallDistance(0);
        player.setHealth(health);
        player.setFoodLevel(food);
        player.setSaturation(saturation);
        player.getInventory().setContents(inventory);
        player.setGameMode(gamemode);

        for (PotionEffect p : player.getActivePotionEffects()) {
            player.removePotionEffect(p.getType());
        }
        player.addPotionEffects(this.potionEffects);
    }

}
