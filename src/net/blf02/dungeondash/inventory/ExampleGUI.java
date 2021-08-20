package net.blf02.dungeondash.inventory;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class ExampleGUI extends BaseGUI {

    public static ExampleGUI instance = new ExampleGUI();

    public ExampleGUI() {
        super(1, "Example GUI");
        this.registerItem(Material.COMMAND_BLOCK, "Example Item", "This is an example item.");
    }

    @Override
    public void onItemClick(ItemStack stack, int slot, HumanEntity player) {
        System.out.println("Stack: " + stack + "\nSlot #: " + slot + "\nPlayer: " + player);
    }
}
