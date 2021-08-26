package net.blf02.dungeondash.inventory;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class ExamplePagedGUI extends PagedGUI {
    public ExamplePagedGUI(String name) {
        super(name);
        for (int i = 0; i < 100; i++) {
            registerItem(Material.BARRIER, "Test #" + i);
        }
    }

    @Override
    public void onItemClick(ItemStack stack, int slot, HumanEntity player) {
        super.onItemClick(stack, slot, player);
        if (stack == null || !stack.hasItemMeta()) return;
        System.out.println("Clicked on item: " + stack.getItemMeta().getDisplayName());
    }
}
