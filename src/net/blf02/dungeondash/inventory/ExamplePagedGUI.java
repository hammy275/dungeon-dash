package net.blf02.dungeondash.inventory;

import net.blf02.dungeondash.DungeonDash;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ExamplePagedGUI extends PagedGUI {
    public ExamplePagedGUI(String name) {
        super(name);
        replaceItem(9 * 5 + 4, Material.COMPASS, ChatColor.GREEN + "Example Bottom Item");
        for (int i = 0; i < 100; i++) {
            registerItem(Material.BARRIER, "Test #" + i);
        }
    }

    @Override
    public void onItemClick(ItemStack stack, int slot, HumanEntity player, final InventoryClickEvent event) {
        super.onItemClick(stack, slot, player, event);
        if (stack == null || !stack.hasItemMeta()) return;
        DungeonDash.logger.info("Clicked on item: " + stack.getItemMeta().getDisplayName());
        event.setCancelled(true);
    }
}
