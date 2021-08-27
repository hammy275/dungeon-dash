package net.blf02.dungeondash.inventory;

import net.blf02.dungeondash.utils.Tracker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Paged GUI.
 *
 * A GUI class with multiple pages.
 *
 * A separate instance of this class must be used per player!
 *
 * The `inv` from the `BaseGUI` class acts as the currently selected inventory.
 */
public abstract class PagedGUI extends BaseGUI {

    protected List<Inventory> invs = new ArrayList<>();
    protected Map<Integer, ItemStack> bottomRowSlots = new HashMap<>();
    private int currentIndex = 0;
    private int currentAmountInLastInv = 0;

    public PagedGUI(String name) {
        super(6, name);
        invs.add(this.inv);
    }

    @Override
    public void registerItem(ItemStack stack) {
        createNewPageIfNeeded().addItem(stack);
        currentAmountInLastInv++;
    }

    @Override
    public void replaceItem(int slot, ItemStack stack) {
        if (slot < 9 * 5) {
            createNewPageIfNeeded().setItem(slot, stack);
            currentAmountInLastInv++;
        } else if (slot == 9 * 5 || slot == 9 * 6 - 1) {
            throw new IllegalArgumentException("Can't set slot for item to next page/previous page slots!");
        } else {
            bottomRowSlots.put(slot, stack);
            for (Inventory i : this.invs) {
                i.setItem(slot, stack);
            }
        }

    }

    /**
     * Creates a new inventory if needed, and returns the last inventory in the list.
     *
     * For example, if the first two pages are full, this will return a third page to add items to.
     *
     * @return Last inventory in the list.
     */
    public Inventory createNewPageIfNeeded() {
        if (currentAmountInLastInv == 9 * 5) {
            Inventory newInv = Bukkit.createInventory(null, 54, this.invName);
            invs.add(newInv);
            currentAmountInLastInv = 0;
            // Add next page and previous page arrows to bottom
            invs.get(invs.size() - 2).setItem(9 * 6 - 1, createItem(Material.ARROW, ChatColor.YELLOW + "Next Page"));
            newInv.setItem(9 * 5, createItem(Material.ARROW, ChatColor.YELLOW + "Previous Page"));
            for (Map.Entry<Integer, ItemStack> entry : this.bottomRowSlots.entrySet()) {
                newInv.setItem(entry.getKey(), entry.getValue());
            }
        }
        return invs.get(invs.size() - 1);
    }

    @Override
    public void onItemClick(ItemStack stack, int slot, HumanEntity player, final InventoryClickEvent event) {
        if (slot == 9 * 5) { // Previous Page
            Tracker.guis.remove(this.inv);
            inv = invs.get(--currentIndex);
            Tracker.guis.put(this.inv, this);
            player.openInventory(this.inv);
        } else if (slot == 9 * 6 - 1) { // Next Page
            Tracker.guis.remove(this.inv);
            inv = invs.get(++currentIndex);
            Tracker.guis.put(this.inv, this);
            player.openInventory(this.inv);
        }
    }
}
