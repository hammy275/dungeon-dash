package net.blf02.dungeondash.event;

import net.blf02.dungeondash.game.PlayerState;
import net.blf02.dungeondash.utils.Tracker;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EventHandler implements Listener {

    @org.bukkit.event.EventHandler
    public void onHurt(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player &&
                ((Player) event.getEntity()).getHealth() - event.getFinalDamage() <= 0) {
            PlayerState state = Tracker.playStatus.get(((Player) event.getEntity()).getDisplayName());
            if (state != null) {
                event.setCancelled(true);
                state.map.doRespawn(state.player, state.inLobby);
            }
        }
    }
}
