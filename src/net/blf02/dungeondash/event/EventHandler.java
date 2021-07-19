package net.blf02.dungeondash.event;

import net.blf02.dungeondash.game.PlayerState;
import net.blf02.dungeondash.utils.Tracker;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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

}

