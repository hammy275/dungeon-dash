package net.blf02.dungeondash.inventory;

import net.blf02.dungeondash.game.DDMap;
import net.blf02.dungeondash.utils.Tracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;


public class JoinGameGUI extends PagedGUI {
    public JoinGameGUI() {
        super("Select a Map");
        for (DDMap map : Tracker.maps) {
            registerItem(map.getGUIIcon());
        }
    }

    @Override
    public void onItemClick(ItemStack stack, int slot, HumanEntity player) {
        super.onItemClick(stack, slot, player);
        if (slot < 9 * 5) {
            Bukkit.getServer().dispatchCommand(player, "ddash join " + stack.getItemMeta().getDisplayName());
            player.closeInventory();
        }

    }
}
