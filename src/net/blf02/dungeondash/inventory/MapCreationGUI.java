package net.blf02.dungeondash.inventory;

import net.blf02.dungeondash.game.CreateState;
import net.blf02.dungeondash.game.DDMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MapCreationGUI extends BaseGUI {

    private final CreateState state;

    public MapCreationGUI(CreateState state) {
        super(1, state.map.mapDisplayName + " Settings");
        this.state = state;
        updateItems();
    }

    public void updateItems() {
        ItemMeta meta;

        ItemStack voidSpawns;
        if (state.map.voidRespawns) {
            voidSpawns = new ItemStack(Material.BEDROCK);
            meta = voidSpawns.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + "Void Respawns - " + ChatColor.GREEN + "Enabled");
        } else {
            voidSpawns = new ItemStack(Material.BARRIER);
            meta = voidSpawns.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + "Void Respawns - " + ChatColor.RED + "Disabled");
        }
        voidSpawns.setItemMeta(meta);
        this.replaceItem(0, voidSpawns);

        ItemStack waterRespawns;
        if (state.map.waterRespawns) {
            waterRespawns = new ItemStack(Material.WATER_BUCKET);
            meta = waterRespawns.getItemMeta();
            meta.setDisplayName(ChatColor.BLUE + "Water Respawns - " + ChatColor.GREEN + "Enabled");
        } else {
            waterRespawns = new ItemStack(Material.BARRIER);
            meta = waterRespawns.getItemMeta();
            meta.setDisplayName(ChatColor.BLUE + "Water Respawns - " + ChatColor.RED + "Disabled");
        }
        waterRespawns.setItemMeta(meta);
        this.replaceItem(1, waterRespawns);

        ItemStack respawnsKill;
        if (state.map.respawnsKill) {
            respawnsKill = new ItemStack(Material.TOTEM_OF_UNDYING);
            meta = respawnsKill.getItemMeta();
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Respawns Kill - " + ChatColor.GREEN + "Enabled");
        } else {
            respawnsKill = new ItemStack(Material.BARRIER);
            meta = respawnsKill.getItemMeta();
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Respawns Kill - " + ChatColor.RED + "Disabled");
        }
        respawnsKill.setItemMeta(meta);
        this.replaceItem(2, respawnsKill);

        ItemStack hasChaser;
        if (state.map.chaserMode == DDMap.ChaserMode.CHASE_LAST) {
            hasChaser = new ItemStack(Material.ARMOR_STAND);
            meta = hasChaser.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + "Use a Chaser - " + ChatColor.GREEN + "Chase Last Place");
        } else if (state.map.chaserMode == DDMap.ChaserMode.SHADOW) {
            hasChaser = new ItemStack(Material.INK_SAC);
            meta = hasChaser.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + "Use a Chaser - " + ChatColor.DARK_GRAY + "Shadow Chases All Players");
        } else if (state.map.chaserMode == DDMap.ChaserMode.NO_CHASER) {
            hasChaser = new ItemStack(Material.BARRIER);
            meta = hasChaser.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + "Use a Chaser - " + ChatColor.RED + "Disabled");
        } else {
            throw new IllegalArgumentException("Map has unimplemented chaser mode!");
        }
        hasChaser.setItemMeta(meta);
        this.replaceItem(3, hasChaser);


        ItemStack changeIcon = this.state.map.getGUIIcon();
        meta = changeIcon.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Change Map Icon");
        changeIcon.setItemMeta(meta);
        this.replaceItem(4, changeIcon);

        ItemStack saveItem = new ItemStack(Material.COMMAND_BLOCK);
        meta = saveItem.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Save Map");
        saveItem.setItemMeta(meta);
        this.replaceItem(7, saveItem);

        ItemStack cancelItem = new ItemStack(Material.BARRIER);
        meta = cancelItem.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "Cancel Creation/Undo Edits");
        cancelItem.setItemMeta(meta);
        this.replaceItem(8, cancelItem);
    }

    @Override
    public void onItemClick(ItemStack stack, int slot, HumanEntity player, final InventoryClickEvent event) {
        String command = null;
        boolean doExit = false;
        switch (slot) {
            case 0:
                command = "ddash create void_respawn";
                break;
            case 1:
                command = "ddash create water_respawn";
                break;
            case 2:
                command = "ddash create respawns_kill";
                break;
            case 3:
                command = "ddash create change_chaser";
                break;
            case 4:
                SetIconGUI setIconGUI = new SetIconGUI(this.state, this);
                setIconGUI.openOnPlayer(player);
                break;
            case 7:
                command = "ddash create save";
                doExit = true;
                break;
            case 8:
                command = "ddash create cancel";
                doExit = true;
                break;
            default:
                System.out.println("Bad option " + slot + "! Please report this to the dev!");
                return;
        }
        if (command != null) {
            Bukkit.getServer().dispatchCommand(state.player, command);
        }
        if (doExit) {
            state.player.closeInventory();
        } else {
            this.updateItems();
        }
        event.setCancelled(true);
    }
}
