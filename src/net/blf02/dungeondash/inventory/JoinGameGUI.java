package net.blf02.dungeondash.inventory;

import net.blf02.dungeondash.game.DDMap;
import net.blf02.dungeondash.utils.Tracker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;


public class JoinGameGUI extends PagedGUI {
    public JoinGameGUI() {
        super("Select a Map");
        for (DDMap map : Tracker.maps) {
            registerItem(map.getGUIIcon());
        }
        replaceItem(9 * 5 + 4, Material.COMPASS, ChatColor.GREEN + "Random Map");
    }

    @Override
    public void onItemClick(ItemStack stack, int slot, HumanEntity player, final InventoryClickEvent event) {
        super.onItemClick(stack, slot, player, event);
        if (slot < 9 * 5) {
            Bukkit.getServer().dispatchCommand(player, "ddash join " + stack.getItemMeta().getDisplayName());
            player.closeInventory();
        } else if (slot == 9 * 5 + 4) {
            DDMap[] maps = Tracker.maps.toArray(new DDMap[0]);
            Bukkit.getServer().dispatchCommand(player, "ddash join " + maps[ThreadLocalRandom.current().nextInt(Tracker.maps.size())].mapDisplayName);
        }
        event.setCancelled(true);
    }
}
