package net.blf02.dungeondash.event;

import net.blf02.dungeondash.game.CreateState;
import net.blf02.dungeondash.game.PlayerState;
import net.blf02.dungeondash.utils.Tracker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
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
        } else if (item == Material.BEDROCK || meta.getDisplayName().contains("Void Respawns")) {
            command = "ddash create void_respawn";
        } else if (item == Material.WATER_BUCKET || meta.getDisplayName().contains("Water Respawns")) {
            command = "ddash create water_respawn";
        } else if (item == Material.TOTEM_OF_UNDYING || meta.getDisplayName().contains("Respawns Kill")) {
            command = "ddash create respawns_kill";
        } else if (item == Material.ARMOR_STAND || meta.getDisplayName().contains("Use a Chaser")) {
            command = "ddash create use_chaser";
        } else if (item == Material.COMMAND_BLOCK) {
            command = "ddash create save";
            doInventoryUpdate = false;
        } else if (item == Material.BARRIER && meta.getDisplayName().contains("Cancel Map Creation")) {
            command = "ddash create cancel";
            doInventoryUpdate = false;
        } else {
            return;
        }
        Bukkit.getServer().dispatchCommand(event.getPlayer(), command);
        if (doInventoryUpdate) {
            state.updateInventory();
        }
    }

}

