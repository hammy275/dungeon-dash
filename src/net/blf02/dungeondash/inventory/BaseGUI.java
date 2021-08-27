package net.blf02.dungeondash.inventory;

import net.blf02.dungeondash.DungeonDash;
import net.blf02.dungeondash.utils.Tracker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class BaseGUI {

    public Inventory inv;
    public BaseGUI lastGUI = null; // If non-null, will open this when the current GUI is closed.
    public final String invName;

    public BaseGUI(int numRows, String name) {
        if (numRows < 1 || numRows > 6) {
            throw new IllegalArgumentException("Must specify 1-6 rows!");
        }
        inv = Bukkit.createInventory(null, numRows * 9, name);
        this.invName = name;
    }

    public void removeItem(int slot) {
        inv.clear(slot);
    }

    public void registerItem(ItemStack stack) {
        inv.addItem(stack);
    }

    public void replaceItem(int slot, ItemStack stack) {
        inv.setItem(slot, stack);
    }

    public void registerItem(final Material material, final String name, @Nullable final String... lore) {
        registerItem(createItem(material, name, lore));
    }

    public void replaceItem(int slot, final Material material, final String name, @Nullable final String... lore) {
        replaceItem(slot, createItem(material, name, lore));
    }

    protected ItemStack createItem(final Material material, final String name, @Nullable final String... lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) {
            meta.setLore(Arrays.asList(lore));
        }
        stack.setItemMeta(meta);
        return stack;
    }

    public void openOnPlayer(Player player) {
        player.closeInventory(); // Close any inventory they may already have open!
        player.openInventory(inv);
        Tracker.guis.put(inv, this);
    }

    public void openOnPlayer(HumanEntity player) {
        openOnPlayer(player, true);
    }

    public void openOnPlayer(HumanEntity player, boolean closeLast) {
        if (closeLast) player.closeInventory(); // Close any inventory they may already have open!
        player.openInventory(inv);
        Tracker.guis.put(inv, this);
    }

    public abstract void onItemClick(ItemStack stack, int slot, HumanEntity player, final InventoryClickEvent event);

    public void onInventoryClose(HumanEntity player) {
        if (lastGUI != null) {
            Bukkit.getScheduler().runTask(DungeonDash.instance, () -> lastGUI.openOnPlayer(player, false));
        }
    }

    public void onInventoryDrag(final InventoryDragEvent event) {
        event.setCancelled(true);
    }
}
