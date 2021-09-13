package net.blf02.dungeondash.inventory;

import net.blf02.dungeondash.DungeonDash;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ExampleGUI extends BaseGUI {

    public static ExampleGUI instance = new ExampleGUI();

    public ExampleGUI() {
        super(1, "Example GUI");
        this.registerItem(Material.COMMAND_BLOCK, "Example Item", "This is an example item.");
    }

    @Override
    public void onItemClick(ItemStack stack, int slot, HumanEntity player, final InventoryClickEvent event) {
        DungeonDash.logger.info("Stack: " + stack + "\nSlot #: " + slot + "\nPlayer: " + player);
        event.setCancelled(true);
    }
}
