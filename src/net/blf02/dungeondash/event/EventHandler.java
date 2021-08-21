package net.blf02.dungeondash.event;

import net.blf02.dungeondash.game.CreateState;
import net.blf02.dungeondash.game.PlayerState;
import net.blf02.dungeondash.inventory.BaseGUI;
import net.blf02.dungeondash.utils.Tracker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class EventHandler implements Listener {

    @org.bukkit.event.EventHandler
    public void onHurt(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player &&
                ((Player) event.getEntity()).getHealth() - event.getFinalDamage() <= 0) {
            PlayerState state = Tracker.playStatus.get(((Player) event.getEntity()).getDisplayName());
            if (state != null) {
                event.setCancelled(true);
                state.doRespawn(state.player, state.inLobby);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onInteraction(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            PlayerState state = Tracker.playStatus.get(event.getPlayer().getDisplayName());
            if (state != null && state.player.getWorld().getBlockAt(state.player.getLocation().add(0, -1, 0)).isEmpty()) return;
            if (state != null) {
                state.respawnPoint = event.getPlayer().getLocation();
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onLeave(PlayerQuitEvent event) {
        // When DC'ing, make sure to remove player's status
        PlayerState state = Tracker.playStatus.get(event.getPlayer().getDisplayName());
        if (state != null) {
            state.leaveGame();
        }
    }

    @org.bukkit.event.EventHandler
    public void onKick(PlayerKickEvent event) {
        // When kicked, make sure to remove player's status
        PlayerState state = Tracker.playStatus.get(event.getPlayer().getDisplayName());
        if (state != null) {
            state.leaveGame();
        }
    }

    @org.bukkit.event.EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Material item = event.getPlayer().getInventory().getItemInMainHand().getType();
        ItemMeta meta = event.getPlayer().getInventory().getItemInMainHand().getItemMeta();
        CreateState state = Tracker.creationStatus.get(event.getPlayer().getDisplayName());
        if (state == null) {
            return;
        }
        boolean doInventoryUpdate = true;
        String command;
        if (item == Material.LIME_DYE) {
            command = "ddash create spawn";
            doInventoryUpdate = false;
        } else if (item == Material.RED_DYE) {
            command = "ddash create end" + state.currentCornerItem;
        } else if (item == Material.COMMAND_BLOCK) {
            command = "ddash create settings";
        } else {
            return;
        }
        Bukkit.getServer().dispatchCommand(event.getPlayer(), command);
        if (doInventoryUpdate) {
            state.updateInventory();
        }
    }

    @org.bukkit.event.EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        BaseGUI gui = Tracker.guis.get(event.getInventory());
        if (gui != null) {
            gui.onInventoryDrag(event);
        }
    }

    @org.bukkit.event.EventHandler
    public void onItemClick(final InventoryClickEvent event) {
        BaseGUI gui = Tracker.guis.get(event.getInventory());
        if (gui != null) {
            if (event.getRawSlot() < gui.inv.getSize()) {
                gui.onItemClick(event.getCurrentItem(), event.getRawSlot(), event.getWhoClicked());
            }
            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        BaseGUI gui = Tracker.guis.get(event.getInventory());
        if (gui != null) {
            Tracker.guis.remove(gui.inv);
        }
    }

}

