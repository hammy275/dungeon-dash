package net.blf02.dungeondash.game;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

public class CreateState {

    public DDMap map;
    public Player player;
    public BeforeGameState beforeGameState;
    public int currentCornerItem = 1;
    public DDMap mapAtInitialRun;

    public CreateState(Player player, DDMap map, DDMap mapAtInitialRun) {
        this.map = map;
        this.player = player;
        this.beforeGameState = new BeforeGameState(player);
        this.mapAtInitialRun = mapAtInitialRun;
        player.setGameMode(GameMode.CREATIVE);
        for (PotionEffect p : player.getActivePotionEffects()) {
            player.removePotionEffect(p.getType());
        }
        updateInventory();
    }

    public void wipeHotbar() {
        for (int i = 0; i < 9; i++) {
            player.getInventory().clear(i);
        }
    }

    public void updateInventory() {
        ItemMeta meta;

        wipeHotbar();

        ItemStack startItem = new ItemStack(Material.LIME_DYE);
        meta = startItem.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Set Start");
        startItem.setItemMeta(meta);

        player.getInventory().setItem(0, startItem);

        ItemStack endItem = new ItemStack(Material.RED_DYE);
        meta = endItem.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Set End Corner " + this.currentCornerItem);
        endItem.setItemMeta(meta);
        player.getInventory().setItem(1, endItem);

        ItemStack settingsItem = new ItemStack(Material.COMMAND_BLOCK);
        meta = settingsItem.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Map Settings");
        settingsItem.setItemMeta(meta);
        player.getInventory().setItem(2, settingsItem);


    }
}
