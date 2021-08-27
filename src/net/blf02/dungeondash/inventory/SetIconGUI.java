package net.blf02.dungeondash.inventory;

import net.blf02.dungeondash.game.CreateState;
import net.blf02.dungeondash.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SetIconGUI extends BaseGUI {

    protected final CreateState state;

    public SetIconGUI(CreateState state, MapCreationGUI lastGUI) {
        super(1, "Set a Map Icon");
        this.state = state;
        for (int i = 0; i < 9; i++) {
            if (i != 4) {
                this.replaceItem(i, Material.GRAY_STAINED_GLASS_PANE, " ");
            } else {
                this.replaceItem(i, this.state.map.getGUIIcon().getType(), ChatColor.GRAY + "Put Item Here",
                        "Put an item representing the map here!");
            }
        }
        this.lastGUI = lastGUI;
    }

    public Material getCurrentMaterial() {
        ItemStack stack = this.inv.getItem(4);
        if (stack == null || stack.getType().isAir()) {
            return null;
        }
        return stack.getType();
    }

    @Override
    public void onItemClick(ItemStack stack, int slot, HumanEntity player, final InventoryClickEvent event) {
        if (slot == 4 && event.getCursor() != null && !event.getCursor().getType().isAir()) {
            this.replaceItem(4, event.getCursor().getType(), ChatColor.GRAY + "Put Item Here", "Put an item representing the map here!");
        }
        event.setCancelled(true);
    }

    @Override
    public void onInventoryClose(HumanEntity player) {
        Material newMat = getCurrentMaterial();
        if (newMat != null) {
            this.state.map.iconId = newMat.toString();
            Util.sendMessage(player, "Successfully changed the icon for " + this.state.map.mapDisplayName);
        } else {
            Util.sendMessage(player, "Failed to change the icon for " + this.state.map.mapDisplayName);
        }
        if (lastGUI != null && lastGUI instanceof MapCreationGUI) {
            ((MapCreationGUI) lastGUI).updateItems();
        }
        super.onInventoryClose(player);
    }
}
