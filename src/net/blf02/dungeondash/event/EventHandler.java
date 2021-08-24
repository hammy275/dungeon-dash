package net.blf02.dungeondash.event;

import net.blf02.dungeondash.game.CreateState;
import net.blf02.dungeondash.game.PlayerState;
import net.blf02.dungeondash.inventory.BaseGUI;
import net.blf02.dungeondash.utils.Config;
import net.blf02.dungeondash.utils.PlayerStorage;
import net.blf02.dungeondash.utils.Tracker;
import net.blf02.dungeondash.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
            state.leaveGame(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onKick(PlayerKickEvent event) {
        // When kicked, make sure to remove player's status
        PlayerState state = Tracker.playStatus.get(event.getPlayer().getDisplayName());
        if (state != null) {
            state.leaveGame(true);
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

    @org.bukkit.event.EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (Config.RESTORE_INVENTORIES) {
            Util.runWithPlayerStorage(event.getPlayer(), () -> attemptInventoryRestore(event.getPlayer()));
        }
    }

    private void attemptInventoryRestore(Player player) {
        PlayerStorage storage = Tracker.playerStorage.get(player.getUniqueId());
        if (storage != null && storage.getBeforeGameState() != null) {
            storage.getBeforeGameState().restoreState(true);
            storage.setBeforeGameState(null);
        }
    }

    @org.bukkit.event.EventHandler
    public void onSave(final WorldSaveEvent event) {
        if (event.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
            saveDDashData(true);
        }
    }

    public static void saveDDashData(boolean doAsync) {
        Set<UUID> toRemove = new HashSet<>();
        for (Map.Entry<UUID, PlayerStorage> entry : Tracker.playerStorage.entrySet()) {
            if (doAsync) {
                entry.getValue().save();
            } else {
                entry.getValue().doSave();
            }
            entry.getValue().savesSinceLastAccess++;
            // Don't remove from "cache" if we're currently in-game (so onDisable can set BeforeGameStates properly without async)
            if (entry.getValue().savesSinceLastAccess >= 5 && Tracker.playStatus.get(entry.getValue().player.getDisplayName()) == null) {
                toRemove.add(entry.getKey());
            } else if (Tracker.playStatus.get(entry.getValue().player.getDisplayName()) != null) {
                entry.getValue().savesSinceLastAccess = 0;
            }
        }
        for (UUID key : toRemove) { // Remove PlayerStorage's from our "cache" after 5 saves.
            Tracker.playerStorage.remove(key);
        }
    }

}

