package net.blf02.dungeondash.inventory;

import net.blf02.dungeondash.utils.Tracker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class BaseGUI {

    public final Inventory inv;

    public BaseGUI(int numRows, String name) {
        if (numRows < 1 || numRows > 6) {
            throw new IllegalArgumentException("Must specify 1-6 rows!");
        }
        inv = Bukkit.createInventory(null, numRows * 9, name);
        Tracker.guis.put(inv, this);
    }

    public void registerItem(final Material material, final String name, @Nullable final String... lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) {
            meta.setLore(Arrays.asList(lore));
        }
        stack.setItemMeta(meta);

        inv.addItem(stack);
    }

    public void openOnPlayer(Player player) {
        player.openInventory(inv);
    }

    public abstract void onItemClick(ItemStack stack, int slot, HumanEntity player);

    public void onInventoryDrag(final InventoryDragEvent event) {
        if (event.getInventory().equals(inv)) {
            event.setCancelled(true);
        }
    }
}
