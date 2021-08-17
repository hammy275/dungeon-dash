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

        ItemStack voidSpawns;
        if (map.voidRespawns) {
            voidSpawns = new ItemStack(Material.BEDROCK);
            meta = voidSpawns.getItemMeta();
            meta.setDisplayName(ChatColor.BLACK + "Void Respawns - " + ChatColor.GREEN + "Enabled");
        } else {
            voidSpawns = new ItemStack(Material.BARRIER);
            meta = voidSpawns.getItemMeta();
            meta.setDisplayName(ChatColor.BLACK + "Void Respawns - " + ChatColor.RED + "Disabled");
        }
        voidSpawns.setItemMeta(meta);
        player.getInventory().setItem(2, voidSpawns);

        ItemStack waterRespawns;
        if (map.waterRespawns) {
            waterRespawns = new ItemStack(Material.WATER_BUCKET);
            meta = waterRespawns.getItemMeta();
            meta.setDisplayName(ChatColor.BLUE + "Water Respawns - " + ChatColor.GREEN + "Enabled");
        } else {
            waterRespawns = new ItemStack(Material.BARRIER);
            meta = waterRespawns.getItemMeta();
            meta.setDisplayName(ChatColor.BLUE + "Water Respawns - " + ChatColor.RED + "Disabled");
        }
        waterRespawns.setItemMeta(meta);
        player.getInventory().setItem(3, waterRespawns);

        ItemStack respawnsKill;
        if (map.respawnsKill) {
            respawnsKill = new ItemStack(Material.TOTEM_OF_UNDYING);
            meta = respawnsKill.getItemMeta();
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Respawns Kill - " + ChatColor.GREEN + "Enabled");
        } else {
            respawnsKill = new ItemStack(Material.BARRIER);
            meta = respawnsKill.getItemMeta();
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Respawns Kill - " + ChatColor.RED + "Disabled");
        }
        respawnsKill.setItemMeta(meta);
        player.getInventory().setItem(4, respawnsKill);

        ItemStack hasChaser;
        if (map.hasChaser) {
            hasChaser = new ItemStack(Material.ARMOR_STAND);
            meta = hasChaser.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + "Use a Chaser - " + ChatColor.GREEN + "Enabled");
        } else {
            hasChaser = new ItemStack(Material.BARRIER);
            meta = hasChaser.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + "Use a Chaser - " + ChatColor.RED + "Disabled");
        }
        hasChaser.setItemMeta(meta);
        player.getInventory().setItem(5, hasChaser);

        ItemStack saveItem = new ItemStack(Material.COMMAND_BLOCK);
        meta = saveItem.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Save Map");
        saveItem.setItemMeta(meta);
        player.getInventory().setItem(6, saveItem);

        ItemStack cancelItem = new ItemStack(Material.BARRIER);
        meta = cancelItem.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "Cancel Map Creation");
        cancelItem.setItemMeta(meta);
        player.getInventory().setItem(8, cancelItem);


    }
}
